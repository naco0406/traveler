
package com.example.traveler

import OuterRouteAdapter
import TripFragment
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class SearchTrip : Fragment() {

    private lateinit var outerRouteAdapter: OuterRouteAdapter
    private val outerRouteListLiveData = MutableLiveData<List<Trip>>()
    private var outerRouteList = mutableListOf<Trip>()
    private var fullRouteList = mutableListOf<Trip>()
    private lateinit var editTextSearch: EditText
    //private var trendyCityName: String? = null
    private var trendyCityName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchTrips()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_trip, container, false)
        editTextSearch = view.findViewById(R.id.searchRoute_editText)
        val filteringButton = view.findViewById<ImageView>(R.id.filteringButton)
        val resetButton = view.findViewById<ImageView>(R.id.reset_filter)


        //fetchTrips()

        outerRouteListLiveData.observe(viewLifecycleOwner, Observer { trips ->
            if (trips.isNotEmpty()) {
                outerRouteList = trips.toMutableList()
                fullRouteList = outerRouteList
                outerRouteAdapter.updateData(outerRouteList)
            }
        })

    // Create OuterRouteAdapter
        outerRouteAdapter = OuterRouteAdapter(requireContext(), outerRouteList)

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
                val searchText = editable.toString()
                if (searchText.isEmpty()) {
                    // If the search text is empty, update the list with the full list
                    updateListWithFullItems()
                } else {
                    // Filter items based on the entered text
                    filterItems(searchText)
                }

            }
        })

        //filtering을 위한 dialogFragment 생성

        filteringButton.setOnClickListener {
            openDialog()

        }


        resetButton.setOnClickListener {
            if (editTextSearch.text != null) {
                editTextSearch.setText("")
            }
            updateListWithFullItems()
        }

        return view
    }


    //Dialog에서 result 받아오는 function

    private fun openDialog() {
        val dialogFragment = FilteringFragment()
        dialogFragment.show(parentFragmentManager, "FilteringFragment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        outerRouteAdapter.onItemClickListener = { trip ->
            startTripFragment(trip)
        }

        parentFragmentManager.setFragmentResultListener(
            "dialog_result",
            viewLifecycleOwner
        ) { _, result ->
            // 결과 수신 시 실행되는 코드
            val numPeople = result.getInt("num_people", 0)
            val period = result.getInt("period", 0)
            Log.d("Receive", "Received Data: $numPeople, and $period(period)")
            filterItems2(numPeople,period)

        }
    }

    override fun onResume() {
        super.onResume()
//        Log.d("Checking","nullable check: $trendyCityName")
//        parentFragmentManager.setFragmentResultListener(
//            "selected_trendy_city",
//            viewLifecycleOwner
//        ) { _, result ->
//            // 결과 수신 시 실행되는 코드
//            trendyCityName = result.getString("selected_trendy_city", "null")
//            Log.d("Receive", "Received Data: $trendyCityName")
//            filterItems(trendyCityName)
//
//        }
    }



    private fun startTripFragment(trip: Trip) {
        val fragment = TripFragment().apply {
            arguments = Bundle().apply {
                putString("tripJson", Gson().toJson(trip))
            }
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.SearchTripContainer, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun filterItems2(target_numPeople: Int, target_period: Int) {
        val fullItemList = outerRouteList/* your original full list of items */


        val filteredList = outerRouteList.filter { trip ->
            trip.period == target_period || trip.numPeople == target_numPeople
        }

        // Filter the items based on the criteria
        //val filteredList = fullItemList.filter { it.city.contains(query, ignoreCase = true) }
//        if (target_numPeople==0 && target_period != 0) {
//            filteredList = outerRouteList.filter { trip->
//                trip.period == target_period
//            }
//        }
//
//        else if (target_numPeople != 0 && target_period == 0) {
//            filteredList = outerRouteList.filter { trip->
//                trip.numPeople == target_numPeople
//            }
//        }
//
//        else if (target_numPeople == 0 && target_period == 0) {
//            filteredList = outerRouteList.filter { trip->
//                trip.numPeople ==target_numPeople || trip.period == target_period
//            }
//        }
//
//
//        else {
//            filteredList = outerRouteList
//        }







        Log.d("Filter2","filtered list2: $filteredList")


        // Update the RecyclerView with the filtered data
        outerRouteAdapter.updateData(filteredList)
    }


    private fun filterItems(query: String) {
        val fullItemList = outerRouteList/* your original full list of items */

        // Filter the items based on the criteria
        //val filteredList = fullItemList.filter { it.city.contains(query, ignoreCase = true) }

        val filteredList = outerRouteList.filter { trip ->
            val placesWithQuery = trip.places.flatten().filter { place ->
                place.city.contains(query, ignoreCase = true) || place.tag.any { tag ->
                    tag.contains(query, ignoreCase = true)
                }
            }
            placesWithQuery.isNotEmpty()
        }


        Log.d("Filter","filtered list: $filteredList")


        // Update the RecyclerView with the filtered data
        outerRouteAdapter.updateData(filteredList)
    }

    private fun updateListWithFullItems() {
        outerRouteAdapter.updateData(fullRouteList)
    }

    private fun fetchTrips() {
        val client = OkHttpClient()
        val serverIp = getString(R.string.server_ip)
        val url = "$serverIp/get_trips"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("TripFragment", "Response from server: $responseBody")

                    val tripListType = object : TypeToken<List<Trip>>() {}.type
                    val trips = Gson().fromJson<List<Trip>>(responseBody, tripListType)
                    val sortedTrips = trips.sortedByDescending { it.selected }

                    if (sortedTrips.isNotEmpty()) {
                        activity?.runOnUiThread {
                            //outerRouteAdapter.updateData(trips)
                            outerRouteListLiveData.postValue(sortedTrips)
                            Log.d("Total","total data: $sortedTrips")
                            //initializeUI()
                        }
                    }
                } else {
                    Log.e("TripFragment", "Response not successful")
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("TripFragment", "Error fetching trips", e)
            }
        })
    }
}