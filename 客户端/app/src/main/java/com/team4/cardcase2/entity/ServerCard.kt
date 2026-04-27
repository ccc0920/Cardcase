package com.team4.cardcase2.entity

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

data class ServerCard(
    val cardId: Int = 0,
    val success: Boolean = false,
    val userId: Int,
    val elements: List<Elements>,
    val avatar: String, // Base64字符串
    val background: String, // Base64字符串
    val design: Design
) {
    companion object {
        // 将JSON字符串转换为Card实例
        fun fromJson(jsonString: String): ServerCard {
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<ServerCard>() {}.type)
        }
    }

    // 将Card实例转换为JSON字符串
    fun toJson(): String {
        val gson = GsonBuilder().create()
        val jsonObject = JsonObject()

        // 按顺序添加字段
        jsonObject.add("userId", gson.toJsonTree(this.userId))

        // 确保元素按顺序添加
        val elementsArray = JsonArray()
        for (element in this.elements) {
            val elementObject = JsonObject()
            elementObject.add("type", gson.toJsonTree(element.type))
            elementObject.add("content", gson.toJsonTree(element.content))

            val positionObject = JsonObject()
            positionObject.add("x", gson.toJsonTree(element.position.x))
            positionObject.add("y", gson.toJsonTree(element.position.y))
            elementObject.add("position", positionObject)

            val styleObject = JsonObject()
            styleObject.add("font", gson.toJsonTree(element.style.font))
            styleObject.add("size", gson.toJsonTree(element.style.size))
            styleObject.add("color", gson.toJsonTree(element.style.color))
            styleObject.add("bold", gson.toJsonTree(element.style.bold))
            styleObject.add("italic", gson.toJsonTree(element.style.italic))
            styleObject.add("underline", gson.toJsonTree(element.style.underline))
            elementObject.add("style", styleObject)

            elementsArray.add(elementObject)
        }
        jsonObject.add("elements", elementsArray)

        jsonObject.add("avatar", gson.toJsonTree(this.avatar))
        jsonObject.add("background", gson.toJsonTree(this.background))

        val designObject = JsonObject()
        designObject.add("templateId", gson.toJsonTree(this.design.templateId))
        designObject.add("color", gson.toJsonTree(this.design.color))
        designObject.add("font", gson.toJsonTree(this.design.font))
        designObject.add("layout", gson.toJsonTree(this.design.layout))
        jsonObject.add("design", designObject)

        return gson.toJson(jsonObject)
    }

    fun out() {
        println(userId)
        println(elements)
    }
}

data class Elements(
    val type: String,
    val content: String,
    val position: Position,
    val style: Style
)

data class Position(
    val x: Int,
    val y: Int
)

data class Style(
    val font: String,
    val size: Int,
    val color: String,
    val bold: Boolean,
    val italic: Boolean,
    val underline: Boolean
)

data class Design(
    val templateId: String,
    val color: String,
    val font: String,
    val layout: String
){
    constructor() : this("", "", "", "")
}