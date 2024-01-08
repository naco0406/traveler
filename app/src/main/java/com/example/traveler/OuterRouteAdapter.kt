import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traveler.R
import com.example.traveler.RouteAdapter

class OuterRouteAdapter(private val OuterRouteList: List<List<String>>):RecyclerView.Adapter<OuterRouteAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.inner_route_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val routeList = OuterRouteList[position]
        holder.bind(routeList)
    }

    override fun getItemCount(): Int {
        return OuterRouteList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.inner_recyclerView)

        fun bind(routeList: List<String>) {
            // Inner RecyclerView Setup
            val innerAdapter = RouteAdapter(routeList)
            innerRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            innerRecyclerView.adapter = innerAdapter
        }
    }
}