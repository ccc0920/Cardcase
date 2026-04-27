package com.team4.cardcase2.entity;

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

data class LoginInfo(
    val email:String,
    val password:String
) {
    companion object {
        // 将JSON字符串转换为Card实例
        fun fromJson(jsonString: String): LoginInfo {
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<LoginInfo>() {}.type)
        }
        fun LoginInfo.toJson(): String {
            val gson = GsonBuilder().create()
            val jsonObject = JsonObject()

            // 添加字段
            jsonObject.add("email", gson.toJsonTree(this.email))
            jsonObject.add("password", gson.toJsonTree(this.password))

            return gson.toJson(jsonObject)
        }
    }
}