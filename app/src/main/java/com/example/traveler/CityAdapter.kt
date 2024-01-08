package com.example.traveler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class CityData (
    val name : String,
    val img : String
)
class CityAdapter(private val context: Context) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

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

        fun bind(item: CityData) {
            txtName.text = item.name
            Glide.with(itemView)
                .load(item.img)
                .into(imgProfile)

        }
    }


}