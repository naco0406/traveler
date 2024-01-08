package com.example.traveler

data class Trip (
    val id: Int,
    val city: String,
    val period: Int,
    val destinations: List<List<String>>,
    val restaurant: List<List<String>>,
    val lodging: List<String>,
    val selected: Int,
    val review: List<String>,
)

