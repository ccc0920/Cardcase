package com.team4.cardcase2.entity

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class UserCardsResponse(
    val success: Boolean,
    val cards: List<ServerCard>
) {
    companion object {
        fun fromJson(jsonString: String): UserCardsResponse {
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<UserCardsResponse>() {}.type)
        }
    }
}
