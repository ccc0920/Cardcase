package com.team4.cardcase2.entity;

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

data class resetpass(
    val email:String,
    val resetCode:String,
    val newPassword:String,
    val confirmPassword:String
) {
    companion object {
        fun resetpass.toJson(): String {
            val gson = GsonBuilder().create()
            val jsonObject = JsonObject()

            // 添加字段
            jsonObject.add("email", gson.toJsonTree(this.email))
            jsonObject.add("resetCode", gson.toJsonTree(this.resetCode))
            jsonObject.add("newPassword", gson.toJsonTree(this.newPassword))
            jsonObject.add("confirmPassword", gson.toJsonTree(this.confirmPassword))

            return gson.toJson(jsonObject)
        }
    }
}