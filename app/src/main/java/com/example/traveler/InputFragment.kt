package com.example.traveler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.traveler.MyTrip
import com.example.traveler.Place
import com.example.traveler.R
import com.example.traveler.Trip
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import java.util.Date

class InputFragment : Fragment(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private var trip: MyTrip = MyTrip("", "", Date(), Date(), mutableListOf(), 0, mutableListOf())
    private lateinit var toolbar: Toolbar
    private lateinit var daysContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        daysContainer = view.findViewById(R.id.daysContainer) // The LinearLayout inside ScrollView
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Map Fragment here
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as com.naver.maps.map.MapFragment?
            ?: com.naver.maps.map.MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_view, it).commit()
            }
        mapFragment.getMapAsync(this)
        setupToolbar()
        addDayEntries()
    }
    private fun addDayEntries() {
        // Suppose you have a list of days or some data structure holding days data
        val daysList = getDaysList() // Implement this method to get your days data

        for (day in daysList) {
            val dayView = LayoutInflater.from(context).inflate(R.layout.item_day_entry, daysContainer, false)

            // Set up the dayView, for example, set the text of TextViews, setup click listeners, etc.
            dayView.findViewById<TextView>(R.id.textViewDay).text = day

            // Add to the container
            daysContainer.addView(dayView)
        }
    }
    private fun getDaysList(): List<String> {
        // This is just an example, replace with actual logic to retrieve days
        return listOf("1.22/화", "1.23/수")
            // Add more days as needed
    }

    private fun setupToolbar() {
        toolbar.title = "여행 일정" // Set toolbar title here
        // If using ActionBarDrawerToggle or need to setup navigation click listener, do it here
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
            locationTrackingMode = LocationTrackingMode.Follow
        }

        setupMapInteractions()

    }

    private fun setupMapInteractions() {
        naverMap.setOnMapClickListener { _, latLng ->
            addMarkerAndPlaceToTrip(latLng)
        }
    }

    private fun addMarkerAndPlaceToTrip(latLng: LatLng) {
        // Create a marker and add to map
        val marker = Marker().apply {
            position = latLng
            map = naverMap
        }

        // Create a Place object and add to trip
        val place = Place(
            name = "해운대 해수욕장",
            city = "부산",
            type = "해변",
            mapx = 129.1586f,
            mapy = 35.1587f,
            address = "해운대구, 부산",
            visited = 0,
            tag = listOf("해변", "관광", "자연"),
            img = R.drawable.img_hud
        )

        trip.places.add(mutableListOf(place))

        Toast.makeText(context, "Added: ${place.name}", Toast.LENGTH_SHORT).show()
    }
}
