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

data class OrderResponse(
    val success: Boolean,
    val orders: List<OrderItem>
) {
    data class OrderItem(
        val orderId: Long,
        val cardId: Long,
        val materials: String,
        val quantity: Int,
        val contactInfo: String,
        val paymentMethod: String,
        val state: String
    )

    companion object {
        fun fromJson(jsonString: String): OrderResponse {
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<OrderResponse>() {}.type)
        }
    }
}
