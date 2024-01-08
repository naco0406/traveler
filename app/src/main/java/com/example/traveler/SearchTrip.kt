
package com.example.traveler

import OuterRouteAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchTrip : Fragment() {

    private lateinit var outerRouteAdapter: OuterRouteAdapter
    private lateinit var outerRouteList: List<Trip>
    private lateinit var editTextSearch: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_trip, container, false)
        editTextSearch = view.findViewById(R.id.searchRoute_editText)

        // Sample data for OuterRouteAdapter
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
        val trip1 = Trip(
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

        val trip2 = Trip(
            id = 2,
            city = "서울",
            period = 2, // 1박 2일
            places = listOf(
                listOf(place1Day2, place1Day2, place1Day2),
                listOf(place1Day1, place2Day1, place1Day1, place2Day1, place1Day1, place2Day1, place1Day1, place2Day1)
            ),
            selected = 0,
            review = listOf(
                "해운대 해수욕장은 정말 멋졌어요!",
                "국제 시장의 다양한 먹거리를 즐겼습니다.",
                "감천문화마을의 예술적인 분위기가 인상적이었어요."
            )
        )

        outerRouteList = listOf<Trip>(trip1, trip2)




    // Create OuterRouteAdapter
        outerRouteAdapter = OuterRouteAdapter(outerRouteList)

        // Set up RecyclerView
        val outerRecyclerView: RecyclerView = view.findViewById(R.id.outer_recyclerView)
        outerRecyclerView.layoutManager = LinearLayoutManager(context)
        outerRecyclerView.adapter = outerRouteAdapter


        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this example
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used in this example
            }

            override fun afterTextChanged(editable: Editable?) {
                // Filter items based on the entered text
                filterItems(editable.toString())
            }
        })

        return view
    }

    private fun filterItems(filterCriteria: String) {
        val fullItemList = outerRouteList/* your original full list of items */

        // Filter the items based on the criteria
        val filteredList = fullItemList.filter { it.city.contains(filterCriteria, ignoreCase = true) }


        // Update the RecyclerView with the filtered data
        outerRouteAdapter.updateData(filteredList)
    }
}