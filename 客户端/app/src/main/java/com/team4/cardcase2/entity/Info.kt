package com.team4.cardcase2.entity;
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

data class Info (
    val fullname:String,
    val email:String,
    val password:String
){

    companion object {
        fun fromJson(jsonString: String): Info {
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<Info>() {}.type)
        }
        fun Info.toJson(): String {
            val gson = GsonBuilder().create()
            val jsonObject = JsonObject()

            // 添加字段
            jsonObject.add("fullname", gson.toJsonTree(this.fullname))
            jsonObject.add("email", gson.toJsonTree(this.email))
            jsonObject.add("password", gson.toJsonTree(this.password))

            return gson.toJson(jsonObject)
        }
    }



}

