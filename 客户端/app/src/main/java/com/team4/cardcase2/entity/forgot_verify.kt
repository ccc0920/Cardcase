package com.team4.cardcase2.entity;

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

data class forgot_verify(
    val email:String
) {
    companion object {
        fun forgot_verify.toJson(): String {
            val gson = GsonBuilder().create()
            val jsonObject = JsonObject()

            // 添加字段
            jsonObject.add("email", gson.toJsonTree(this.email))

            return gson.toJson(jsonObject)
        }
    }
}