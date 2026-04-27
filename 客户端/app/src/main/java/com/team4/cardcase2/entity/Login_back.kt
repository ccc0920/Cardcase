package com.team4.cardcase2.entity;

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Login_back(
    val success:Boolean,
    val userId:Int,
    val token:String,
    val message:String
) {
    companion object {
        fun fromJson(jsonString: String): Login_back {
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<Login_back>() {}.type)
        }
    }
}