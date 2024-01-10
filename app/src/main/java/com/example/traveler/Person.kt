package com.example.traveler

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.json.JSONObject

@Serializable
data class Person(
    val name: String="Unknown",
    val phone: String="",
    val nickname: String="Nickname",
    val password: String="",
)
fun parseJson(jsonString: String): Person {
    return Json.decodeFromString(jsonString)
}

fun toJson(person: Person): String {
    val jsonObject = JSONObject()
    jsonObject.put("name", person.name)
    jsonObject.put("phone", person.phone)
    jsonObject.put("nickname", person.nickname)
    jsonObject.put("password", person.password)
    return jsonObject.toString()
}