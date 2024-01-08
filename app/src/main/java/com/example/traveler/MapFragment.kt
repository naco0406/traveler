package com.example.traveler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var resultTextView: TextView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        // Inflate the layout for this fragment
        resultTextView = view.findViewById(R.id.textview_test)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as com.naver.maps.map.MapFragment?
            ?: com.naver.maps.map.MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // 지도 설정
        naverMap.apply {
            maxZoom = 18.0
            minZoom = 10.0
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true)
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, true)
            uiSettings.isLocationButtonEnabled = true
            locationTrackingMode = LocationTrackingMode.Follow
            locationSource = this@MapFragment.locationSource
        }

        // 초기 위치 설정
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.4979921, 127.028046)).animate(
            CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

        naverMap.setOnMapClickListener { pointF, latLng ->
            // 'latLng'에서 위도와 경도를 얻습니다.
            val latitude = latLng.latitude
            val longitude = latLng.longitude

            reverseGeocoding(latitude, longitude) { address ->
                if (address.isNotEmpty()) {
                    searchNaverLocal(address) { searchResult ->
                        val restaurants = parseJsonToRestaurants(searchResult)
                        activity?.runOnUiThread {
                            addMarkersAndAdjustCamera(restaurants, naverMap, latLng)
                        }
                    }
                }
            }

        }
    }
    fun reverseGeocoding(latitude: Double, longitude: Double, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=$longitude,$latitude&output=json"
        val clientId = getString(R.string.naver_maps_client_id)
        val clientSecret = getString(R.string.naver_maps_client_secret)

        val request = Request.Builder()
            .url(url)
            .addHeader("X-NCP-APIGW-API-KEY-ID", clientId)
            .addHeader("X-NCP-APIGW-API-KEY", clientSecret)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val address = parseAddressFromJson(responseBody) // 주소 파싱 로직
                    Log.d("reverseGeocoding", "onResponse: $address")
                    callback(address)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }
    fun parseAddressFromJson(jsonString: String): String {
        val jsonObj = JSONObject(jsonString)
        val results = jsonObj.getJSONArray("results")
        if (results.length() > 0) {
            val region = results.getJSONObject(0).getJSONObject("region")
            val area1 = region.getJSONObject("area1").getString("name") // 시/도
            val area2 = region.getJSONObject("area2").getString("name") // 구/군
            val area3 = region.getJSONObject("area3").getString("name") // 동/면/읍
            Log.d("parseAddressFromJson", "onResponse: $area1 $area2 $area3")
            return "$area1 $area2 $area3"
        }
        return ""
    }
    fun searchNaverLocal(address: String, callback: (String) -> Unit) {
        val query = URLEncoder.encode("$address 가까운 식당", "UTF-8")
        val url = "https://openapi.naver.com/v1/search/local.json?query=$query&display=5&start=1&sort=random"
        val clientId = getString(R.string.naver_search_client_id)
        val clientSecret = getString(R.string.naver_search_client_secret)

        val request = Request.Builder()
            .url(url)
            .addHeader("X-Naver-Client-Id", clientId)
            .addHeader("X-Naver-Client-Secret", clientSecret)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("searchNaverLocal", "onResponse: $response")
                val responseData = response.body?.string()

                Log.d("searchNaverLocal", "onResponseBody: $responseData")
                // 응답 처리
                activity?.runOnUiThread {
                    callback(responseData ?: "No response")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // 오류 처리
            }
        })
    }

    fun parseJsonToRestaurants(jsonString: String?): List<Restaurant> {
        val resultList = mutableListOf<Restaurant>()
        val jsonObj = JSONObject(jsonString)
        val itemsArray = jsonObj.getJSONArray("items")

        for (i in 0 until itemsArray.length()) {
            val item = itemsArray.getJSONObject(i)
            val restaurant = Restaurant(
                title = item.getString("title"),
                link = item.getString("link"),
                category = item.getString("category"),
                description = item.getString("description"),
                telephone = item.getString("telephone"),
                address = item.getString("address"),
                roadAddress = item.getString("roadAddress"),
                mapx = item.getLong("mapx"),
                mapy = item.getLong("mapy")
            )
            resultList.add(restaurant)
        }

        return resultList
    }

    fun addMarkersAndAdjustCamera(restaurants: List<Restaurant>, naverMap: NaverMap, initialLatLng: LatLng) {

        val bounds = LatLngBounds.Builder()
        bounds.include(initialLatLng) // 사용자가 터치한 위치도 포함

        val restaurantDistanceList = mutableListOf<Pair<Restaurant, Double>>()

        // 사용자가 터치한 위치에 마커 추가
        val touchMarker = Marker().apply {
            position = initialLatLng
            map = naverMap
            onClickListener = Overlay.OnClickListener {
                val sortedRestaurants = restaurantDistanceList.sortedBy { it.second }.map { it.first }
                val topRestaurants = sortedRestaurants.take(5).joinToString("\n") { it.title }
                resultTextView.text = topRestaurants
                true
            }
        }

        for (restaurant in restaurants) {
            val latitude = restaurant.mapy.toDouble() / 1_000_0000
            val longitude = restaurant.mapx.toDouble() / 1_000_0000
            val marker = Marker()
            Log.d("addMarkersToNaverMap", "addMarkersToNaverMap: $latitude, $longitude")
            val restaurantLatLng = LatLng(latitude, longitude)
            val distance = initialLatLng.distanceTo(restaurantLatLng)
            restaurantDistanceList.add(Pair(restaurant, distance))
            marker.position = restaurantLatLng
            marker.map = naverMap
            marker.onClickListener = Overlay.OnClickListener {
                // 여기에 마커 터치 시 동작을 정의합니다.
                // 예: 텍스트 뷰에 식당 이름 표시
                resultTextView.text = "Selected: ${restaurant.title}"
                true
            }

            bounds.include(LatLng(latitude, longitude))
        }

        // 초기 터치 지점을 지도의 중심으로 설정
        val initialCameraUpdate = CameraUpdate.scrollTo(initialLatLng).animate(CameraAnimation.Easing)
        naverMap.moveCamera(initialCameraUpdate)

        val cameraUpdate = CameraUpdate.fitBounds(bounds.build()).animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

        val zoomOutUpdate = CameraUpdate.zoomOut().animate(CameraAnimation.Easing)
        naverMap.moveCamera(zoomOutUpdate)
    }

}
