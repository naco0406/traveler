
package com.example.traveler

import OuterRouteAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchTrip : Fragment() {

    private lateinit var outerRouteAdapter: OuterRouteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_trip, container, false)

        // Sample data for OuterRouteAdapter
        val outerRouteList = listOf(
            listOf("Place A", "Place B", "Place C","Place D", "Place E", "Place F"),
            listOf("Place D", "Place E","Place F", "Place G", "Place H"),
            listOf("Place I", "Place J", "Place K", "Place L", "Place M")

        )

        // Create OuterRouteAdapter
        outerRouteAdapter = OuterRouteAdapter(outerRouteList)

        // Set up RecyclerView
        val outerRecyclerView: RecyclerView = view.findViewById(R.id.outer_recyclerView)
        outerRecyclerView.layoutManager = LinearLayoutManager(context)
        outerRecyclerView.adapter = outerRouteAdapter

        return view
    }
}