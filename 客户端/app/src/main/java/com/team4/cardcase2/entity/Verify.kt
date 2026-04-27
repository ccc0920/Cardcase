package com.team4.cardcase2.entity;

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

data class Verify(
    val email:String,
    val verificationCode:String,
) {

    companion object {
        fun fromJson(jsonString: String): Verify {
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<Verify>() {}.type)
        }
        fun Verify.toJson(): String {
            val gson = GsonBuilder().create()
            val jsonObject = JsonObject()

            // 添加字段
            jsonObject.add("email", gson.toJsonTree(this.email))
            jsonObject.add("verificationCode", gson.toJsonTree(this.verificationCode))

            return gson.toJson(jsonObject)
        }
    }
}