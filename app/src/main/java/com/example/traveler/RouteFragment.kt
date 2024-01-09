
package com.example.traveler

import OuterRouteAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RouteFragment(private var routeData: Trip) : Fragment() {

    private lateinit var RouteAdapter: RouteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.inner_route_item, container, false)
        val likeButton = view.findViewById<ToggleButton>(R.id.likeButton)

        // Sample data for OuterRouteAdapter
        // Create OuterRouteAdapter
        val places = routeData.places[0]
        RouteAdapter = RouteAdapter(places)

        // Set up RecyclerView
        val innerRecyclerView: RecyclerView = view.findViewById(R.id.inner_recyclerView)
        innerRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        innerRecyclerView.adapter = RouteAdapter


        return view
    }


}