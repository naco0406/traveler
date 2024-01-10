import android.content.ContentValues.TAG
import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traveler.R
import com.example.traveler.RouteAdapter
import com.example.traveler.Trip
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class OuterRouteAdapter(private val context: Context, private var OuterRouteList: List<Trip>):RecyclerView.Adapter<OuterRouteAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.inner_route_item, parent, false)

        return ViewHolder(itemView)
    }

    var onItemClickListener: ((Trip) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val route = OuterRouteList[position]
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(route)
        }
        holder.bind(route)
    }

    override fun getItemCount(): Int {
        return OuterRouteList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.inner_recyclerView)
        val likeButton = itemView.findViewById<ToggleButton>(R.id.likeButton)
        val selectedValue = itemView.findViewById<TextView>(R.id.selectedValue)

        init {
            // ToggleButton의 상태가 변경될 때의 동작 정의
            likeButton.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // 아이템의 위치(position)을 사용하여 작업 수행
                    handleToggleButton(position, isChecked)
                }
            }
        }

        fun bind(routeList: Trip) {
            // Inner RecyclerView Setup
            selectedValue.text = routeList.selected.toString()
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

    private fun handleToggleButton(position: Int, isChecked: Boolean) {
        // 이 메서드에서 position을 사용하여 필요한 동작 수행
        // 예를 들어, itemList[position]에 대한 작업 수행 가능
        if (position != RecyclerView.NO_POSITION) {
            // itemList에서 해당하는 Trip 객체 가져오기
            val selectedTrip = OuterRouteList[position]
            if (isChecked) {
                selectedTrip.selected += 1
            } else {
                if (selectedTrip.selected > 0) {
                    selectedTrip.selected -= 1
                }
            }
            Log.d("Checking changes", "id of trip: ${selectedTrip.id}")
            updateSelectedValue(selectedTrip)

        }
    }

    private fun updateSelectedValue(changedTrip: Trip) {
        val client = OkHttpClient()
        val gson = Gson()
        val jsonTrip = gson.toJson(changedTrip)
        val jsonString = jsonTrip.toString()
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonString)
        val serverIp = context.getString(R.string.server_ip)
        val url = "$serverIp/updateSelectedValue"
        Log.d("check url","checking url: $url")
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d(TAG, responseBody ?: "")
                } else {
                    // 서버 응답이 실패한 경우 처리
                    Log.e(TAG, "Server responded with error: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Network request failed: ${e.message}")
                // 오류 처리를 원하는대로 수행
            }
        })

    }

}