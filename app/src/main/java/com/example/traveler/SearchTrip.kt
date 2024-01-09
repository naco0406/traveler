
package com.example.traveler

import OuterRouteAdapter
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_trip, container, false)
        editTextSearch = view.findViewById(R.id.searchRoute_editText)


        fetchTrips()

        outerRouteListLiveData.observe(viewLifecycleOwner, Observer { trips ->
            if (trips.isNotEmpty()) {
                outerRouteList = trips.toMutableList()
                fullRouteList = outerRouteList
                outerRouteAdapter.updateData(outerRouteList)
            }
        })





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

        return view
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

                    if (trips.isNotEmpty()) {
                        activity?.runOnUiThread {
                            //outerRouteAdapter.updateData(trips)
                            outerRouteListLiveData.postValue(trips)
                            Log.d("Total","total data: $trips")
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