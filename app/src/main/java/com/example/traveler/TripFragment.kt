// TripFragment.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.traveler.Place
import com.example.traveler.R
import com.example.traveler.Trip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker

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
    private lateinit var trip: Trip
    private val markers = mutableListOf<Marker>()
    private lateinit var naverMap: NaverMap
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        naverMap.apply {
            maxZoom = 18.0
            minZoom = 10.0
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true)
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, true)
            uiSettings.isLocationButtonEnabled = true
            locationTrackingMode = LocationTrackingMode.Follow
        }

        val initialPlaces = listOf<Place>() // 이곳에 초기 위치 데이터를 넣으세요
        addMarkersToMap(initialPlaces)
        // 나머지 설정...
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 데이터 로드 로직
        val place1Day1 = Place(
            name = "해운대 해수욕장",
            city = "부산",
            type = "해변",
            mapx = 129.1586f,
            mapy = 35.1587f,
            address = "해운대구, 부산",
            visited = 0,
            tag = listOf("해변", "관광", "자연"),
            img = R.drawable.img_hud // 예시 이미지 리소스
        )

        val place2Day1 = Place(
            name = "부산 국제 시장",
            city = "부산",
            type = "시장",
            mapx = 129.0225f,
            mapy = 35.1028f,
            address = "중구, 부산",
            visited = 0,
            tag = listOf("쇼핑", "음식", "문화"),
            img = R.drawable.img_hud // 예시 이미지 리소스
        )

        val place1Day2 = Place(
            name = "감천문화마을",
            city = "부산",
            type = "문화마을",
            mapx = 129.0104f,
            mapy = 35.0975f,
            address = "사하구, 부산",
            visited = 0,
            tag = listOf("문화", "예술", "역사"),
            img = R.drawable.img_hud // 예시 이미지 리소스
        )

// 여행 일정 구성
        trip = Trip(
            id = 1,
            city = "부산",
            period = 2, // 1박 2일
            places = listOf(
                listOf(place1Day1, place2Day1, place1Day1, place2Day1, place1Day1, place2Day1, place1Day1, place2Day1),
                listOf(place1Day2, place1Day2, place1Day2)
            ),
            selected = 0,
            review = listOf(
                "해운대 해수욕장은 정말 멋졌어요!",
                "국제 시장의 다양한 먹거리를 즐겼습니다.",
                "감천문화마을의 예술적인 분위기가 인상적이었어요."
            )
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        tabs = view.findViewById(R.id.tabs)

        // ViewPager2와 TabLayout 설정
        viewPager.adapter = TripViewPagerAdapter(requireActivity(), trip)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = "Day ${position + 1}"
        }.attach()

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateMarkersForPosition(position)
            }
        })
    }

    private fun isMapInitialized(): Boolean {
        return this::naverMap.isInitialized
    }

    private fun updateMarkersForPosition(position: Int) {
        if (isMapInitialized()) {
            clearMarkers() // 이전 마커들을 모두 제거
            addMarkersToMap(trip.places[position]) // 새 위치에 대한 마커들 추가
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
        clearMarkers()
        if (places.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            for (place in places) {
                val latLng = LatLng(place.mapy.toDouble(), place.mapx.toDouble())
                val marker = Marker().apply {
                    position = latLng
                    map = naverMap
                }
                markers.add(marker)
                boundsBuilder.include(latLng)
            }

            val bounds = boundsBuilder.build()
            val cameraUpdate = CameraUpdate.fitBounds(bounds)
            naverMap.moveCamera(cameraUpdate)
        } else {
            // 좌표가 없을 경우, 기본 위치로 카메라를 이동하거나 다른 처리를 수행
            // 예: naverMap.moveCamera(CameraUpdate.scrollTo(DEFAULT_LAT_LNG))
        }
    }

    private fun clearMarkers() {
        for (marker in markers) {
            marker.map = null // 마커 제거
        }
        markers.clear()
    }
}
