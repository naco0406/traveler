package com.example.traveler

import BottomTripFragment
import TripFragment
import android.animation.LayoutTransition
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException



class SecondFragment : Fragment(), OnItemClickListener {

    private lateinit var cityAdapter: CityAdapter
    private lateinit var placeAdapter: PlaceAdapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var trendy_city_name = ""

    private fun isUserLoggedIn(): Boolean {
        val fileName = "user_status.json"
        val file = File(requireActivity().applicationContext.filesDir, fileName)
        if (!file.exists()) return false

        return try {
            val content = file.readText()
            val userData = JSONObject(content)
            userData.getBoolean("login_state")
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

//    private fun setupBottomSheet() {
//        val bottomSheet = view?.findViewById<LinearLayout>(R.id.bottom_sheet)
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//
//        if (!isUserLoggedIn()) {
//            // 로그인 상태가 아닐 경우
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            bottomSheetBehavior.isDraggable = false
//
//            // 로그인 요청 메시지 표시
//            showLoginRequiredMessage()
//        } else {
//            // 로그인 상태일 경우
//            bottomSheetBehavior.isDraggable = true
//            bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height_month)
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        }
//    }

    private fun showLoginRequiredMessage() {
        // 여기에서 로그인하라는 메시지를 표시하거나, 로그인 요청을 위한 UI 요소를 활성화
        val loginRequiredTextView = view?.findViewById<TextView>(R.id.loginRequiredTextView)
        loginRequiredTextView?.visibility = View.VISIBLE
        loginRequiredTextView?.text = "로그인이 필요한 기능입니다."
    }

    override fun onResume() {
        super.onResume()
        if (isUserLoggedIn()) {
            fetchClosestFutureMyTrip()
        }
    }

    private fun fetchClosestFutureMyTrip() {
        val client = OkHttpClient()
        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/get_closest_future_mytrip"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val myTrip = Gson().fromJson(responseBody, MyTrip::class.java)
                    activity?.runOnUiThread {
                        displayMyTrip(myTrip)
                    }
                } else {
                    Log.e("SecondFragment", "Server responded with error")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("SecondFragment", "Error fetching closest future mytrip", e)
            }
        })
    }

    private fun displayMyTrip(myTrip: MyTrip) {
        val bottomSheetContent = requireActivity().findViewById<LinearLayout>(R.id.bottomSheetContent)

        val bottomTripFragment = BottomTripFragment().apply {
            arguments = Bundle().apply {
                putString("myTripJson", Gson().toJson(myTrip))
            }
        }

        // TripFragment를 FrameLayout에 동적으로 추가
        childFragmentManager.beginTransaction().apply {
            val frameLayout = FrameLayout(requireContext()).apply {
                id = View.generateViewId() // 동적으로 ID 생성
            }
            bottomSheetContent.addView(frameLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            replace(frameLayout.id, bottomTripFragment)
            commit()
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottom_sheet)
        val scrollView = view.findViewById<ScrollView>(R.id.lockScrollView)
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val bottomSheetCityCard = view.findViewById<CardView>(R.id.bottomSheetCityImage)
        val bottomSheetImageSpace = view.findViewById<FrameLayout>(R.id.bottomSheetImageSpace)
        val bottomSheetToolbar = view.findViewById<LinearLayout>(R.id.bottomSheetToolbar)
        val bottomSheetMapButton = view.findViewById<ImageButton>(R.id.bottomSheetMapButton)
        val bottomSheetInfo = view.findViewById<LinearLayout>(R.id.bottomSheetInfo)
        val bottomSheetContent = view.findViewById<LinearLayout>(R.id.bottomSheetContent)

//        setupBottomSheet()

//        val bottomtripFragment = BottomTripFragment().apply {
//            arguments = Bundle().apply {
//                // 필요한 경우 TripFragment에 전달할 데이터 설정
//            }
//        }
//
//        // Fragment를 동적으로 추가하기 위한 Transaction 시작
//        childFragmentManager.beginTransaction().apply {
//            // LinearLayout 내부에 FrameLayout을 추가하여 그 안에 Fragment를 삽입
//            val frameLayout = FrameLayout(requireContext()).apply {
//                id = R.id.bottom_framelayout // 동적으로 ID 생성
//            }
//            bottomSheetContent.addView(frameLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//
//            // FrameLayout에 TripFragment 추가
//            replace(frameLayout.id, bottomtripFragment)
//            commit()
//        }

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height_month)
//        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        Log.d("bottomSheet", "Half Expanded")
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d("bottomSheet", "Expanded")
                        scrollView.isVisible = false
                        toolbar.isVisible = false
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d("bottomSheet", "Collapsed")
                        scrollView.isVisible = true
                        toolbar.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                scrollView.isVisible = true
                toolbar.isVisible = true

                // 슬라이드 오프셋에 따라서 가로 길이를 변경
                val startWidth = 75f // 시작 가로 길이 (50dp)
                val endWidth = 350f // 끝 가로 길이 (300dp)
                val newWidth = startWidth + (endWidth - startWidth) * slideOffset
                val widthInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newWidth, resources.displayMetrics).toInt()
                val layoutParams = bottomSheetCityCard.layoutParams
                layoutParams.width = widthInPixels
                layoutParams.height = widthInPixels
                bottomSheetCityCard.layoutParams = layoutParams

                // 슬라이드 오프셋에 따라서 이미지 모서리 둥근 정도를 변경
                val startRadius = 10f // 시작 모서리 둥글기 (10dp)
                val endRadius = 30f // 끝 모서리 둥글기 (30dp)
                val newRadius = startRadius + (endRadius - startRadius) * slideOffset
                val radiusInPixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, newRadius, resources.displayMetrics
                )
                bottomSheetCityCard.radius = radiusInPixels

                // 슬라이드 오프셋에 따라서 툴바 높이를 변경
                val startToolbarHeight = 0f
                val endToolbarHeight = 40f
                val newToolbarHeight = startToolbarHeight + (endToolbarHeight - startToolbarHeight) * slideOffset
                val toolbarHeightInPixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, newToolbarHeight, resources.displayMetrics
                ).toInt()
                val toolbarLayoutParams = bottomSheetToolbar.layoutParams
                toolbarLayoutParams.height = toolbarHeightInPixels
                bottomSheetToolbar.layoutParams = toolbarLayoutParams

                // 슬라이드 오프셋에 따라서 이미지를 가운데 정렬
                val startImageSpaceWidth = 250f
                val endImageSpaceWidth = 0f
                val newImageSpaceWidth = startImageSpaceWidth + (endImageSpaceWidth - startImageSpaceWidth) * slideOffset
                val imageSpaceWidthInPixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, newImageSpaceWidth, resources.displayMetrics
                ).toInt()
                val imageSpaceLayoutParams = bottomSheetImageSpace.layoutParams
                imageSpaceLayoutParams.width = imageSpaceWidthInPixels
                bottomSheetImageSpace.layoutParams = imageSpaceLayoutParams

                // 슬라이드 오프셋에 따라 지도 버튼, 정보 표시의 투명도 조절
                val startAlpha = 1f // 시작 투명도 (완전 불투명)
                val endAlpha = 0f // 끝 투명도 (완전 투명)
                val newAlpha = startAlpha + (endAlpha - startAlpha) * slideOffset * 2
                val adjustedAlpha = if (newAlpha < 0) 0f else newAlpha
                bottomSheetMapButton.alpha = adjustedAlpha
                bottomSheetInfo.alpha = adjustedAlpha

                // 슬라이드 오프셋에 따라서 내용 표시의 투명도 조절
                val startContentAlpha = 0f // 시작 투명도 (완전 투명)
                val endContentAlpha = 1f // 끝 투명도 (완전 불투명)
                val newContentAlpha = startContentAlpha + (endContentAlpha - startContentAlpha) * slideOffset
                bottomSheetContent.alpha = newContentAlpha


            }
        })

//        bottomSheet.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    // 터치가 시작될 때 ScrollView 비활성화
//                    scrollView.isEnabled = false
//                }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                    // 손이 떨어지거나 터치가 취소될 때 ScrollView 활성화
//                    scrollView.isEnabled = true
//                }
//            }
//            true
//        }

        val rv_city = view.findViewById<RecyclerView>(R.id.recyclerViewCity)

        cityAdapter = CityAdapter(requireContext(), this )

        rv_city.adapter = cityAdapter
        context.let {
            rv_city.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        fetchCities { cities ->
            cityAdapter.datas = cities.toMutableList()
            activity?.runOnUiThread {
                cityAdapter.notifyDataSetChanged()
            }
        }


        val rv_place = view.findViewById<RecyclerView>(R.id.recyclerViewPlace)
        placeAdapter = PlaceAdapter(requireContext())

        rv_place.adapter = placeAdapter
        context.let {
            rv_place.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        Log.d("placeDataList", "fetchPlaces")
        fetchPlaces { places ->
            placeAdapter.datas = places.toMutableList()
            activity?.runOnUiThread {
                placeAdapter.notifyDataSetChanged()
            }
        }

    }

    override fun onItemClick(trendy_city_name: String) {
        // 해당 아이템을 클릭했을 때의 동작 수행
        // 여기에서 Fragment2로 이동하는 코드 추가

        //val bundle = Bundle()
        Log.d("Checking","passed trendy_city_name: $trendy_city_name")
        //bundle.putString("text_from_clicked_item", trendy_city_name)  // 데이터를 Bundle에 추가
        //fragment2.arguments = bundle
        //val searchTripFragment = SearchTrip()
        //searchTripFragment.initializeWithTrendyCity(trendy_city_name)


        // 새로운 SearchTrip fragment를 생성하고 trendyCityName을 설정
        val searchTripFragment = SearchTrip.newInstance(trendy_city_name)


        // ViewPager2의 currentItem을 변경하는 코드
        activity?.let { nonNullActivity ->
            val viewPager2 = nonNullActivity.findViewById<ViewPager2>(R.id.viewPager)
            viewPager2?.currentItem = 1 // 1은 Fragment2의 인덱스, 0부터 시작
        }



    }
    private fun fetchCities(callback: (List<CityData>) -> Unit) {
        val client = OkHttpClient()
        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/get_cities_rank"
        val request = Request.Builder()
            .url(url) // 서버의 URL로 변경하세요.
            .get() // 데이터를 가져오는 GET 요청입니다.
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonString ->

                    val cityDataList = parseCityData(jsonString) // JSON을 파싱하는 함수를 호출합니다.
                    Log.d("placeCityList", cityDataList.toString())
                    callback(cityDataList)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // 오류 처리
                e.printStackTrace()
            }
        })
    }

    private fun parseCityData(jsonString: String): List<CityData> {
        val cityDataList = mutableListOf<CityData>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val image = jsonObject.getString("image")
                cityDataList.add(CityData(name = name, img = image))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return cityDataList
    }
    private fun fetchPlaces(callback: (List<PlaceData>) -> Unit) {
        val client = OkHttpClient()
        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/get_places_rank"
        val request = Request.Builder()
            .url(url) // 서버의 URL로 변경하세요.
            .get() // 데이터를 가져오는 GET 요청입니다.
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonString ->
                    val placeDataList = parsePlaceData(jsonString) // JSON을 파싱하는 함수를 호출합니다.
                    Log.d("placeDataList", placeDataList.toString())
                    callback(placeDataList)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // 오류 처리
                e.printStackTrace()
            }
        })
    }
    private fun parsePlaceData(jsonString: String): List<PlaceData> {
        val placeDataList = mutableListOf<PlaceData>()
        try {
            val jsonArray = JSONArray(jsonString)
//            Log.d("placeDataList1", jsonArray.toString())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val img = jsonObject.getString("img")
                placeDataList.add(PlaceData(name = name, img = img))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return placeDataList
    }


}