
package com.example.traveler

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class PlaceSearchAdapter(private var places: List<Place>) : RecyclerView.Adapter<PlaceSearchAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        // Add other views you might have
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.textViewName.text = place.name
        // Bind other data to views
    }

    override fun getItemCount() = places.size

    fun updateData(newPlaces: List<Place>) {
        places = newPlaces
        notifyDataSetChanged()
    }
}

class SearchPlaceFragment : Fragment() {
    private lateinit var placeSearchAdapter: PlaceSearchAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextSearch: EditText
    private var placeList = mutableListOf<Place>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_place, container, false)

        editTextSearch = view.findViewById(R.id.editTextSearch)
        recyclerView = view.findViewById(R.id.recyclerViewPlaces)
        recyclerView.layoutManager = LinearLayoutManager(context)
        placeSearchAdapter = PlaceSearchAdapter(placeList)
        recyclerView.adapter = placeSearchAdapter

        setupSearchBar()
        fetchPlaces()  // Implement this method to fetch places from your server or local database

        return view
    }

    private fun setupSearchBar() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                val searchText = editable.toString().trim()
                filterPlaces(searchText)
            }
        })
    }

    private fun filterPlaces(query: String) {
        val filteredList = if (query.isEmpty()) {
            placeList
        } else {
            placeList.filter { it.name.contains(query, ignoreCase = true) }
        }
        placeSearchAdapter.updateData(filteredList)
    }

    private fun fetchPlaces() {
        // Fetch places and update placeList
        val client = OkHttpClient()
        val serverIp = getString(R.string.server_ip) // Ensure you have your server IP defined in strings.xml
        val url = "$serverIp/get_places" // The endpoint on your Flask server

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val gson = Gson()
                    val placeListType = object : TypeToken<List<Place>>() {}.type
                    val places = gson.fromJson<List<Place>>(responseBody, placeListType)
//                    Log.d("placesLog", "$places")

                    activity?.runOnUiThread {
                        placeList.clear()
                        placeList.addAll(places)
                        placeSearchAdapter.updateData(placeList)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace() // Handle the error
            }
        })
    }
}
