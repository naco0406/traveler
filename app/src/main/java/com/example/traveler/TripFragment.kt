// TripFragment.kt

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.traveler.MyTrip
import com.example.traveler.Place
import com.example.traveler.R
import com.example.traveler.Trip
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Calendar
import java.util.Date

class TripAdapter(private val trip: Trip) : RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // destinations, restaurant, lodging 항목에 따라 뷰 바인딩 구현
        val allItems = trip.places.flatten()

        // 현재 위치에 맞는 데이터를 뷰에 바인딩
        holder.textView.text = allItems[position].toString()

        val context = holder.itemView.context

    }

    private val totalItemCount: Int by lazy {
        trip.places.flatten().size
    }

    override fun getItemCount(): Int = totalItemCount

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 뷰 홀더 구성 요소 초기화
        val textView: TextView = view.findViewById(R.id.textView)
    }
}
class TripFragment : Fragment(), OnMapReadyCallback {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabs: TabLayout
    private var trip: Trip? = null
    private val markers = mutableListOf<Marker>()
    private lateinit var naverMap: NaverMap
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

        trip?.let { currentTrip ->
            if (currentTrip.places.isNotEmpty()) {
                addMarkersToMap(currentTrip.places[0])
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        fetchTrips()
//
//        val client = OkHttpClient()
//        val serverIp = getString(R.string.server_ip)
//        val url = "$serverIp/get_trips"
//        val request = Request.Builder()
//            .url(url)
//            .build()

        arguments?.getString("tripJson")?.let {
            trip = Gson().fromJson(it, Trip::class.java)
        }

    }
//    private fun fetchTrips() {
//        val client = OkHttpClient()
//        val serverIp = getString(R.string.server_ip)
//        val url = "$serverIp/get_trips"
//        val request = Request.Builder().url(url).build()
//
//        client.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body?.string()
//                    Log.d("TripFragment", "Response from server: $responseBody")
//
//                    val tripListType = object : TypeToken<List<Trip>>() {}.type
//                    val trips = Gson().fromJson<List<Trip>>(responseBody, tripListType)
//
//                    if (trips.isNotEmpty()) {
//                        activity?.runOnUiThread {
//                            trip = trips[0]
//                            initializeUI()
//                        }
//                    }
//                } else {
//                    Log.e("TripFragment", "Response not successful")
//                }
//            }
//
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                Log.e("TripFragment", "Error fetching trips", e)
//            }
//        })
//    }
    private fun initializeUI() {
        trip?.let { currentTrip ->
            viewPager.adapter = TripViewPagerAdapter(requireActivity(), currentTrip)
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = "Day ${position + 1}"
            }.attach()

            if (isMapInitialized()) {
                addMarkersToMap(currentTrip.places[0])
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip, container, false)

        viewPager = view.findViewById(R.id.viewPager)
        tabs = view.findViewById(R.id.tabs)

        // ViewPager2와 TabLayout 설정을 이곳으로 이동
        trip?.let { currentTrip ->
            viewPager.adapter = TripViewPagerAdapter(requireActivity(), currentTrip)
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

    override fun onResume() {
        super.onResume()
        isUserLoggedIn()
    }
    private fun isUserLoggedIn(): Boolean {
        val fileName = "user_status.json"
        val file = File(requireActivity().applicationContext.filesDir, fileName)
        if (!file.exists()) return false

        return try {
            val content = file.readText()
            val userData = JSONObject(content)
            Log.d("isUserLogin", "$userData")
            val isUserLogin = userData.getBoolean("login_state")
            Log.d("isUserLogin", "$isUserLogin")
            isUserLogin
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateMarkersForPosition(position)
            }
        })
        initializeUI()
        val importButton = view.findViewById<FloatingActionButton>(R.id.ImportButton)
        importButton.setOnClickListener {
//            if (isUserLoggedIn()) {
//                checkAndSaveTrip()
//            } else {
//                showLoginAlert()
//            }
            checkAndSaveTrip()
        }
    }

    private fun showLoginAlert() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("로그인 필요")
                .setMessage("이 기능을 사용하기 위해서는 먼저 로그인해야 합니다.")
                .setPositiveButton("확인", null)
                .show()
        }
    }

    private fun checkAndSaveTrip() {
        val fileName = "MyTrip.json"
        val internalStorageDir = requireActivity().applicationContext.filesDir
        val file = File(internalStorageDir, fileName)

        if (file.exists()) {
            // 파일이 존재하면 경고 메시지 표시
            showAlertDialog(file)
        } else {
            // 파일이 존재하지 않으면 trip 데이터를 저장
            showDatePicker(file)
        }
    }

    private fun showAlertDialog(file: File) {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("경고")
                .setMessage("이미 MyTrip.json 파일이 존재합니다. 덮어씌우시겠습니까?")
                .setPositiveButton("예") { dialog, which ->
                    showDatePicker(file)
                }
                .setNegativeButton("아니오", null)
                .show()
        }
    }

    private fun showDatePicker(file: File) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                trip?.let { currentTrip ->
                    val myTrip = convertTripToMyTrip(currentTrip, calendar.time)
                    saveMyTripToFile(file, myTrip)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun convertTripToMyTrip(trip: Trip, startDate: Date): MyTrip {
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        val endDate = Calendar.getInstance().apply {
            time = startDate
            add(Calendar.DAY_OF_MONTH, trip.period)
        }.time

        return MyTrip(
            id = trip.id,
            city = trip.city,
            start_date = startDate,
            end_date = endDate,
            places = trip.places.map { it.toMutableList() }.toMutableList(),
            selected = trip.selected,
            review = trip.review.toMutableList()
        )
    }

    private fun saveMyTripToFile(file: File, myTrip: MyTrip) {
        val myTripJson = Gson().toJson(myTrip)
        file.writeText(myTripJson)
        // 저장 완료 메시지 또는 다른 후속 조치
    }


    private fun isMapInitialized(): Boolean {
        return this::naverMap.isInitialized
    }

    private fun updateMarkersForPosition(position: Int) {
        if (isMapInitialized()) {
            trip?.let { currentTrip ->
                if (position < currentTrip.places.size) {
                    val dayActivities = currentTrip.places[position]
                    clearMarkers() // 이전 마커들을 모두 제거
                    addMarkersToMap(dayActivities) // 선택된 위치에 대한 마커들 추가
                }
            }
        }
    }

    // ViewPager2 어댑터
    private inner class TripViewPagerAdapter(fa: FragmentActivity, private val trip: Trip) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = trip.places.size

        override fun createFragment(position: Int): Fragment {
            // 각 포지션에 맞는 TripDayFragment 생성
            val dayActivities = trip.places[position]
            return TripDayFragment.newInstance(dayActivities)
        }
    }

    private fun addMarkersToMap(places: List<Place>) {
        if (places.isNotEmpty()) {
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

    private fun getCustomIconResource(number: Int): Int {
        // 이 메서드는 숫자에 따라 다른 이미지 리소스를 반환합니다.
        // 예시를 위해 임의의 리소스를 반환하도록 설정합니다.
        // 실제로는 숫자에 따라 적절한 이미지 리소스 ID를 반환해야 합니다.
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
