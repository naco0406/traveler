package com.example.traveler

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
@Serializable
data class Person(
    val name: String,
    val phone: String,
    val nickName: String,
)
fun parseJson(jsonString: String): Person {
    return Json.decodeFromString(jsonString)
}