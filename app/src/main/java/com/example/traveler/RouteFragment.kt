
package com.example.traveler

import OuterRouteAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RouteFragment : Fragment() {

    private lateinit var RouteAdapter: RouteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.inner_route_item, container, false)

        // Sample data for OuterRouteAdapter
        val RouteList = listOf("Place A", "Place B", "Place C")
        // Create OuterRouteAdapter
        RouteAdapter = RouteAdapter(RouteList)

        // Set up RecyclerView
        val innerRecyclerView: RecyclerView = view.findViewById(R.id.inner_recyclerView)
        innerRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        innerRecyclerView.adapter = RouteAdapter

        return view
    }
}