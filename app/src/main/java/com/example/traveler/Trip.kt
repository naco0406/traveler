package com.example.traveler

data class Trip (
    val id: String,
    val city: String,
    val period: Int,
    val places: List<List<Place>>,
    var selected: Int,
    var review: List<String>,
    val numPeople: Int,
)