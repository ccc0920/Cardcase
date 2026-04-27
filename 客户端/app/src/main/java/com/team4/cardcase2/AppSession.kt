package com.team4.cardcase2

import android.content.Context
import android.content.SharedPreferences

object AppSession {
    private const val PREFS_NAME = "auth"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "userId"
    private const val KEY_EMAIL = "email"

    fun saveLoginInfo(context: Context, userId: Int, token: String, email: String = "") {
        prefs(context).edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getToken(context: Context): String = prefs(context).getString(KEY_TOKEN, "") ?: ""

    fun getUserId(context: Context): Int = prefs(context).getInt(KEY_USER_ID, 0)

    fun getEmail(context: Context): String = prefs(context).getString(KEY_EMAIL, "") ?: ""

    fun isLoggedIn(context: Context): Boolean = getToken(context).isNotEmpty()

    fun logout(context: Context) {
        prefs(context).edit().clear().apply()
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
