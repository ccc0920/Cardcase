package com.team4.cardcase2.foreground

interface ButtonClickListener {
suspend fun rowClick(id: Int, text: String)
}