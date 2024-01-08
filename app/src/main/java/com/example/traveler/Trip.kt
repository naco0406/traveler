package com.example.traveler

data class Trip (
    val id: String,
    val city: String,
    val period: Int,
    val places: List<List<Place>>,
    val selected: Int,
    val review: List<String>,
)