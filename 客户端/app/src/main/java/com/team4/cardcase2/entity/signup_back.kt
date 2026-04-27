package com.team4.cardcase2.entity;

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class signup_back(
    val success:Boolean,
    val message:String
) {
    companion object {
        fun fromJson(jsonString: String): signup_back {
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<signup_back>() {}.type)
        }
    }
}