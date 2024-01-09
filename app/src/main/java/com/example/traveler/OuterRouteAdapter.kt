import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traveler.R
import com.example.traveler.RouteAdapter
import com.example.traveler.Trip

class OuterRouteAdapter(private var OuterRouteList: List<Trip>):RecyclerView.Adapter<OuterRouteAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.inner_route_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val route = OuterRouteList[position]
        holder.bind(route)
    }

    override fun getItemCount(): Int {
        return OuterRouteList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.inner_recyclerView)

        fun bind(routeList: Trip) {
            // Inner RecyclerView Setup
            val places = routeList.places[0]
            val innerAdapter = RouteAdapter(places)
            innerRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            innerRecyclerView.adapter = innerAdapter
        }
    }

    fun updateData(newItemList: List<Trip>) {
        OuterRouteList = newItemList
        val OuterRouteList_size = OuterRouteList.size
        Log.d("Filter","updated list size: $OuterRouteList_size, updated list: $OuterRouteList,")
        notifyDataSetChanged()
    }
}