package com.example.traveler
import java.util.Date

data class MyTrip (
    var id: String,
    var city: String,
    var start_date: Date,
    var end_date: Date,
    var places: MutableList<MutableList<Place>>,
    var selected: Int,
    var review: MutableList<String>
)