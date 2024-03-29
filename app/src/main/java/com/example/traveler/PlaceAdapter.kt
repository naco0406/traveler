package com.example.traveler

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class PlaceData (
    val name : String,
    val img : String
)

class PlaceAdapter(private val context: Context) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    var datas = mutableListOf<PlaceData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.main_place,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

//        private val txtNumber: TextView = itemView.findViewById(R.id.rv_number)
        private val txtName: TextView = itemView.findViewById(R.id.rv_name)
        private val imgProfile: ImageView = itemView.findViewById(R.id.rv_photo)


        fun bind(item: PlaceData) {
            Log.d("PlaceImageLoad", item.img.toString())
            txtName.text = item.name
            Glide.with(itemView)
                .load(item.img)
                .into(imgProfile)

        }
    }


}