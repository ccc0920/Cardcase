package com.team4.cardcase2.entity

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class WholeServerCard(
    val success: Boolean,
    val card: ServerCard
){

    companion object {
        // 将JSON字符串转换为Card实例
        fun fromJson(jsonString: String): WholeServerCard {
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<WholeServerCard>() {}.type)
        }
    }
}
