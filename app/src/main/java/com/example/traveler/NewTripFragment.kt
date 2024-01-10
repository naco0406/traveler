package com.example.traveler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class NewTripFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.save_trip_button).setOnClickListener {
            val city = view.findViewById<EditText>(R.id.city_input).text.toString()
            val startDate = view.findViewById<EditText>(R.id.start_date_input).text.toString()
            val endDate = view.findViewById<EditText>(R.id.end_date_input).text.toString()

            if (city.isBlank() || startDate.isBlank() || endDate.isBlank()) {
                Toast.makeText(context, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
            } else {
                val myTrip = createMyTripObject(city, startDate, endDate)
                saveMyTripToFile(myTrip)
                // 다른 프래그먼트로 전환하거나 필요한 추가 처리 수행
                val inputFragment = InputFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.InputContainer, inputFragment)
                    .commit()
            }
        }
    }
    private fun createMyTripObject(city: String, startDate: String, endDate: String): MyTrip {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = sdf.parse(startDate)
        val end = sdf.parse(endDate)
        val days = getDaysDifference(start, end)

        val places = MutableList(days) { mutableListOf<Place>() }

        return MyTrip(
            id = UUID.randomUUID().toString(), // 고유 ID 생성
            city = city,
            start_date = start,
            end_date = end,
            places = places,
            selected = 0,
            review = mutableListOf()
        )
    }

    private fun getDaysDifference(start: Date?, end: Date?): Int {
        if (start == null || end == null) return 0

        val calendarStart = Calendar.getInstance().apply { time = start }
        val calendarEnd = Calendar.getInstance().apply { time = end }
        val daysBetween = calendarEnd.get(Calendar.DAY_OF_YEAR) - calendarStart.get(Calendar.DAY_OF_YEAR) + 1
        return Math.max(daysBetween, 1)
    }

    private fun saveMyTripToFile(myTrip: MyTrip) {
        val gson = Gson()
        val myTripJson = gson.toJson(myTrip)
        val fileName = "MyTrip.json"
        val internalStorageDir = requireActivity().applicationContext.filesDir
        val file = File(internalStorageDir, fileName)
        file.writeText(myTripJson)
    }

}
