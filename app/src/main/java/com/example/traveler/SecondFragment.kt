package com.example.traveler

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_city = view.findViewById<RecyclerView>(R.id.recyclerViewCity)
        cityAdapter = CityAdapter(requireContext())

        rv_city.adapter = cityAdapter
        context.let {
            rv_city.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        data_city.apply {
            add(CityData(name = "Seoul", img = R.drawable.icon_my))
            add(CityData(name = "Busan", img = R.drawable.icon_my))
            add(CityData(name = "Daejeon", img = R.drawable.icon_my))
            add(CityData(name = "Ulsan", img = R.drawable.icon_my))
            add(CityData(name = "Gwangju", img = R.drawable.icon_my))
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
                placeDataList.add(PlaceData(name = name, img = R.drawable.icon_my))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return placeDataList
    }


}