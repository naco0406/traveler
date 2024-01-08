package com.example.traveler

data class Trip (
    val id: Int,
    val city: String,
    val period: Int,
    val places: List<List<String>>,
    val selected: Int,
    val review: List<String>,
)

