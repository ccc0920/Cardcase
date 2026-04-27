package com.team4.cardcase2.entity;

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class verify_back(
    val success:Boolean,
    val userId:Int,
    val message:String
) {
    companion object {
        fun fromJson(jsonString: String): verify_back {
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<verify_back>() {}.type)
        }
    }
}