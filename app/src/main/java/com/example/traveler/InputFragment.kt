package com.example.traveler

import OnPlaceUpdateListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.traveler.MyTrip
import com.example.traveler.Place
import com.example.traveler.R
import com.example.traveler.Trip
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Date

class MyTripAdapter(private val mytrip: MyTrip) : RecyclerView.Adapter<MyTripAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mytrip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // destinations, restaurant, lodging 항목에 따라 뷰 바인딩 구현
        val allItems = mytrip.places.flatten()

        // 현재 위치에 맞는 데이터를 뷰에 바인딩
        holder.textView.text = allItems[position].toString()

        val context = holder.itemView.context

    }

    private val totalItemCount: Int by lazy {
        mytrip.places.flatten().size
    }

    override fun getItemCount(): Int = totalItemCount

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 뷰 홀더 구성 요소 초기화
        val textView: TextView = view.findViewById(R.id.textView)
    }
}
class InputFragment : Fragment(), OnMapReadyCallback, OnPlaceUpdateListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabs: TabLayout
    private var mytrip: MyTrip? = null
    private val markers = mutableListOf<Marker>()
    private lateinit var naverMap: NaverMap
    private lateinit var toolbar: Toolbar
    private lateinit var daysContainer: LinearLayout
    private var currentTabPosition: Int = 0 // 현재 탭 위치를 저장하는 전역 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        fetchMyTrips("010-5678-9012")

        if (isMyTripFileExists()) {
            loadMyTripFromFile()
            updateMarkersAndCamera()
        } else {
            openNewTripFragment() // 새 여행 경로를 생성하는 프래그먼트 열기
        }

    }
    private fun isMyTripFileExists(): Boolean {
        val fileName = "MyTrip.json"
        val internalStorageDir = requireActivity().applicationContext.filesDir
        val file = File(internalStorageDir, fileName)
        return file.exists()
    }

    private fun loadMyTripFromFile() {
        val fileName = "MyTrip.json"
        val internalStorageDir = requireActivity().applicationContext.filesDir
        val file = File(internalStorageDir, fileName)
        try {
            val content = file.readText()
            val gson = Gson()
            mytrip = gson.fromJson(content, MyTrip::class.java)
            // UI 초기화
            initializeUI()
        } catch (e: Exception) {
            e.printStackTrace()
            openNewTripFragment() // 파일 로드 실패 시 새 여행 경로 생성 프래그먼트 열기
        }
    }

    private fun openNewTripFragment() {
        // 새 여행 경로를 생성하는 프래그먼트로 전환
        if (!isMyTripFileExists()) {
            val newTripFragment = NewTripFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.InputContainer, newTripFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)
        toolbar = view.findViewById(R.id.toolbar)
//        daysContainer = view.findViewById(R.id.daysContainer) // The LinearLayout inside ScrollView
        viewPager = view.findViewById(R.id.viewPager)
        tabs = view.findViewById(R.id.tabs)

        // ViewPager2와 TabLayout 설정
        mytrip?.let { currentMyTrip ->
            viewPager.adapter = MyTripViewPagerAdapter(requireActivity(), currentMyTrip)
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = "Day ${position + 1}"
            }.attach()
        }

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        return view
    }

    private fun handlePlaceSelected(place: Place) {
        // 여기에 장소를 처리하는 로직을 추가합니다.
        // 예를 들면, mytrip.places에 장소 추가 또는 변경 등
        Log.d("handlePlaceSelected", "$place")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Map Fragment here
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as com.naver.maps.map.MapFragment?
            ?: com.naver.maps.map.MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        // ViewPager2 및 TabLayout 찾기
        viewPager = view.findViewById(R.id.viewPager)
        tabs = view.findViewById(R.id.tabs)

        // ViewPager2 설정
        mytrip?.let { currentMyTrip ->
            viewPager.adapter = MyTripViewPagerAdapter(requireActivity(), currentMyTrip)
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = "Day ${position + 1}"
            }.attach()
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentTabPosition = position
                updateMarkersForPosition(position)
            }
        })

        val searchPlaceFragment = SearchPlaceFragment().apply {
            onPlaceSelected = { place ->
                handlePlaceSelected(place)
            }
        }

        setupToolbar()
        val saveMyTripButton = view.findViewById<Button>(R.id.saveMyTripButton)
        saveMyTripButton.setOnClickListener {
            uploadTripData()
        }
    }

    private fun uploadTripData() {
        val loginfileName = "Mypage.json"
        val internalStorageDir = requireActivity().applicationContext.getFilesDir()
        val loginfile = File(internalStorageDir, loginfileName)
        val logincontent = loginfile.readText()
        val myData = JSONObject(logincontent)
        val myphone = myData.getString("phone")

        val fileName = "MyTrip.json"
//        val internalStorageDir = requireActivity().applicationContext.getFilesDir()
        val file = File(internalStorageDir, fileName)
        if (file.exists()) {
            val jsonContent = file.readText()
            uploadDataToServer(jsonContent, myphone) { success ->
                if (success) {
                    file.delete() // 파일 삭제
//                    Toast.makeText(context, "Trip data uploaded and local file deleted.", Toast.LENGTH_LONG).show()
                } else {
//                    Toast.makeText(context, "Failed to upload trip data.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun uploadDataToServer(jsonData: String, phone: String, callback: (Boolean) -> Unit) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("phone", phone)
            put("mytrip", JSONObject(jsonData))
        }
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = json.toString().toRequestBody(mediaType)

//        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonData)
        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/upload_mytrip_data"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (isMyTripFileExists() && this::naverMap.isInitialized) {
            loadMyTripFromFile()
            updateMarkersAndCamera()
        } else if (!isMyTripFileExists()) {
            openNewTripFragment()
        }
    }

    private fun isMapInitialized(): Boolean {
        return this::naverMap.isInitialized
    }

    private fun updateMarkersForPosition(position: Int) {
        if (isMapInitialized()) {
            if(this::viewPager.isInitialized) {
                mytrip?.let { currentMyTrip ->
                    val selectedPosition = viewPager.currentItem
                    if (selectedPosition < currentMyTrip.places.size) {
                        val dayActivities = currentMyTrip.places[selectedPosition]
                        clearMarkers()
                        addMarkersToMap(dayActivities)
                    }
                }
            }
        }
    }

    private fun fetchMyTrips(phoneNumber: String) {
        val client = OkHttpClient()
        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/get_user_mytrips"

        // Create the JSON payload with the user's phone number
        val json = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(json, "{\"phone\":\"$phoneNumber\"}")

        // Build the request as a POST
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // Make the asynchronous POST request
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("TripFragmentInput", "Response from server: $responseBody")

                    val mytripListType = object : TypeToken<List<MyTrip>>() {}.type
                    val mytrips = Gson().fromJson<List<MyTrip>>(responseBody, mytripListType)

                    val fileName = "MyTrip.json"
                    val internalStorageDir = requireActivity().applicationContext.getFilesDir()
                    val file = File(internalStorageDir, fileName)

                    val jsonArray = JSONArray(responseBody)
                    val lastObjectIndex = jsonArray.length() - 1 // 배열의 마지막 인덱스
                    val myTripObject = jsonArray.getJSONObject(lastObjectIndex) // 마지막 객체 추출
                    file.writeText(myTripObject.toString())



                    if (mytrips.isNotEmpty()) {
                        activity?.runOnUiThread {
                            val fileName = "MyTrip.json"
                            val internalStorageDir = requireActivity().applicationContext.getFilesDir()
                            val file = File(internalStorageDir, fileName)

                            try {
                                val content = file.readText()
                                Log.e("Load JSON", "저장되어 있던 my data 불러오기: $content")
                                val gson = Gson()
                                val myData = JSONObject(content)
                                mytrip = gson.fromJson(myData.toString(), MyTrip::class.java)
                                Log.d("Load JSON", myData.getString("places").toString())
                            } catch (e: ApiException) {
                            }
//                            mytrip = mytrips[0]
                            initializeUI()
                        }
                    }
                    activity?.runOnUiThread {
                        // Update the UI with the list of trips
                        // This assumes you have a RecyclerView or other UI elements to display the trips
                        updateUIWithTrips(mytrips)
                    }
                } else {
                    Log.e("TripFragmentInput", "Response not successful: ${response.message}")
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("TripFragmentInput", "Error fetching trips", e)
            }
        })
    }

    private fun updateUIWithTrips(mytrips: List<MyTrip>) {
//         TODO: Implement the UI update logic
        // For example, updating a RecyclerView adapter:
//         myTripsAdapter.updateData(mytrips)
    }

    private fun initializeUI() {
        mytrip?.let { currentMyTrip ->
            viewPager.adapter = MyTripViewPagerAdapter(requireActivity(), currentMyTrip)
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = "Day ${position + 1}"
            }.attach()

            if (isMapInitialized()) {
                addMarkersToMap(currentMyTrip.places[0])
            }
        }
    }

    private fun setupToolbar() {
        if (isMyTripFileExists()) {
            val fileName = "MyTrip.json"
            val internalStorageDir = requireActivity().applicationContext.getFilesDir()
            val file = File(internalStorageDir, fileName)
            var city = ""

            try {
                val content = file.readText()
                Log.e("Load JSON", "저장되어 있던 my data 불러오기: $content")
                val gson = Gson()
                val myData = JSONObject(content)
                mytrip = gson.fromJson(myData.toString(), MyTrip::class.java)
                city = myData.getString("city").toString()
            } catch (e: ApiException) {
            }
            toolbar.title = "$city 여행 일정" // Set toolbar title here
        } else {
            toolbar.title = "여행 일정" // Set toolbar title here
        }
    }
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.uiSettings.isLocationButtonEnabled = false
        naverMap.uiSettings.isZoomControlEnabled = false
        naverMap.uiSettings.isScaleBarEnabled = false

        naverMap.apply {
            maxZoom = 18.0
            minZoom = 10.0
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true)
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, true)
            locationTrackingMode = LocationTrackingMode.Follow
        }

        mytrip?.let { currentMyTrip ->
            if (currentMyTrip.places.isNotEmpty()) {
                addMarkersToMap(currentMyTrip.places[0])
            }
        }

        if (isMyTripFileExists()) {
            loadMyTripFromFile()
            updateMarkersAndCamera()
        }

    }
    private fun openSearchPlaceFragment() {
        val searchPlaceFragment = SearchPlaceFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.InputContainer, searchPlaceFragment) // 'container'는 호스트 액티비티의 FragmentContainerView ID
            .addToBackStack(null)
            .commit()
    }

//    private fun setupMapInteractions() {
//        naverMap.setOnMapClickListener { _, latLng ->
//            addMarkerAndPlaceToMyTrip(latLng)
//        }
//    }
//
//    private fun addMarkerAndPlaceToMyTrip(latLng: LatLng) {
//        // Create a marker and add to map
//        val marker = Marker().apply {
//            position = latLng
//            map = naverMap
//        }
//
//        // Create a Place object and add to trip
//        val place = Place(
//            name = "해운대 해수욕장",
//            city = "부산",
//            type = "해변",
//            mapx = 129.1586f,
//            mapy = 35.1587f,
//            address = "해운대구, 부산",
//            visited = 0,
//            tag = listOf("해변", "관광", "자연"),
//            img = R.drawable.img_hud
//        )
//
//        mytrip?.places?.add(mutableListOf(place))
//
//        Toast.makeText(context, "Added: ${place.name}", Toast.LENGTH_SHORT).show()
//    }
    private inner class MyTripViewPagerAdapter(fa: FragmentActivity, private val mytrip: MyTrip) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = mytrip.places.size

        override fun createFragment(position: Int): Fragment {
            // 각 포지션에 맞는 TripDayFragment 생성
            val dayActivities = mytrip.places[position]
            val dayFragment = MyTripDayFragment.newInstance(dayActivities, position, ::openSearchPlaceFragment)
            dayFragment.placeUpdateListener = this@InputFragment
            return dayFragment
//            return MyTripDayFragment.newInstance(dayActivities, ::openSearchPlaceFragment)
        }
    }

    private fun addMarkersToMap(places: List<Place>) {
        if (this::naverMap.isInitialized && places.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            for ((index, place) in places.withIndex()) {
                val latLng = LatLng(place.mapx.toDouble(), place.mapy.toDouble())
                Log.d("latLng", "latLng: $latLng")
                val marker = Marker().apply {
                    position = latLng
                    icon = OverlayImage.fromResource(getCustomIconResource(index + 1))
                    map = naverMap
                }
                markers.add(marker)
                boundsBuilder.include(latLng)
            }
            val bounds = boundsBuilder.build()

            val cameraUpdate = CameraUpdate.fitBounds(bounds)
            naverMap.moveCamera(cameraUpdate)

            val zoomOutUpdate = CameraUpdate.zoomOut().animate(CameraAnimation.Easing)
            naverMap.moveCamera(zoomOutUpdate)

        } else {
            // 좌표가 없을 경우, 기본 위치로 카메라를 이동하거나 다른 처리를 수행
            // 예: naverMap.moveCamera(CameraUpdate.scrollTo(DEFAULT_LAT_LNG))
        }
    }

    override fun onPlaceUpdated() {
        updateMarkersAndCamera()
    }

    private fun updateMarkersAndCamera() {
        // viewPager가 초기화되었는지 확인
        if (this::naverMap.isInitialized && this::viewPager.isInitialized) {
//            val currentPosition = viewPager.currentItem

            mytrip?.let { currentMyTrip ->
                // 현재 선택된 탭의 위치에 따라 마커와 카메라 업데이트
                val selectedPosition = viewPager.currentItem
                if (selectedPosition < currentMyTrip.places.size) {
                    val dayActivities = currentMyTrip.places[selectedPosition]
                    clearMarkers()
                    addMarkersToMap(dayActivities)
                }
            }
            viewPager.currentItem = currentTabPosition

        } else {
            // viewPager가 초기화되지 않았다면 로그 기록 또는 오류 처리
            Log.e("InputFragment", "ViewPager not initialized")
        }
    }


    private fun getCustomIconResource(number: Int): Int {
        // 이 메서드는 숫자에 따라 다른 이미지 리소스를 반환합니다.
        // 예시를 위해 임의의 리소스를 반환하도록 설정합니다.
        return when(number) {
            1 -> R.drawable.marker_1
            2 -> R.drawable.marker_2
            3 -> R.drawable.marker_3
            4 -> R.drawable.marker_4
            5 -> R.drawable.marker_5
            6 -> R.drawable.marker_6
            7 -> R.drawable.marker_7
            8 -> R.drawable.marker_8
            9 -> R.drawable.marker_9
            10 -> R.drawable.marker_10
            11 -> R.drawable.marker_11
            12 -> R.drawable.marker_12
            13 -> R.drawable.marker_13
            14 -> R.drawable.marker_14
            15 -> R.drawable.marker_15
            else -> R.drawable.round_location_on_24
        }
    }


    private fun clearMarkers() {
        for (marker in markers) {
            marker.map = null // 마커 제거
        }
        markers.clear()
    }
}