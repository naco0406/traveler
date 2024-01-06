package com.example.traveler

import android.animation.LayoutTransition
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class SecondFragment : Fragment() {

    private lateinit var cityAdapter: CityAdapter
    val data_city = mutableListOf<CityData>()

    private lateinit var placeAdapter: PlaceAdapter
    val data_place = mutableListOf<PlaceData>()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
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

        val placelinearLayout = view.findViewById<LinearLayout>(R.id.placelinearLayout)
        placelinearLayout.layoutTransition = LayoutTransition()

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
//                        placelinearLayout.orientation = LinearLayout.VERTICAL
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d("bottomSheet", "Collapsed")
                        scrollView.isVisible = true
                        toolbar.isVisible = true
//                        placelinearLayout.orientation = LinearLayout.HORIZONTAL
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                scrollView.isVisible = true
                toolbar.isVisible = true
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
        cityAdapter = CityAdapter(requireContext())

        rv_city.adapter = cityAdapter
        context.let {
            rv_city.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        data_city.apply {
            add(CityData(name = "서울", img = R.drawable.img_gwm))
            add(CityData(name = "부산", img = R.drawable.img_hud))
            add(CityData(name = "대전", img = R.drawable.img_gwm))
            add(CityData(name = "울산", img = R.drawable.img_hud))
            add(CityData(name = "광주", img = R.drawable.img_gwm))
            add(CityData(name = "대구", img = R.drawable.img_hud))
            add(CityData(name = "인천", img = R.drawable.img_gwm))
            add(CityData(name = "세종", img = R.drawable.img_hud))
        }
        cityAdapter.datas = data_city
        cityAdapter.notifyDataSetChanged()


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

    private fun fetchPlaces(callback: (List<PlaceData>) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://43.200.170.97:5000/get_places") // 서버의 URL로 변경하세요.
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
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                placeDataList.add(PlaceData(name = name, img = R.drawable.img_gwm))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return placeDataList
    }


}