package com.example.traveler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class CityData (
    val name : String,
    val img : String
)
class CityAdapter(private val context: Context, private val listener: OnItemClickListener) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    var datas = mutableListOf<CityData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.main_city,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtName: TextView = itemView.findViewById(R.id.rv_name)
        private val imgProfile: ImageView = itemView.findViewById(R.id.rv_photo)
        private val trendy_city: FrameLayout = itemView.findViewById(R.id.trendy_city)

        init {
            trendy_city.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // 아이템의 위치(position)을 사용하여 작업 수행
                    val clicked_city_name = datas[position].name
                    listener.onItemClick(clicked_city_name)
                }
            }
        }

        fun bind(item: CityData) {
            txtName.text = item.name
            Glide.with(itemView)
                .load(item.img)
                .into(imgProfile)

        }
    }


}