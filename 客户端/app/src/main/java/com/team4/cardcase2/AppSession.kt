package com.team4.cardcase2

import android.content.Context
import android.content.SharedPreferences

object AppSession {
    private const val PREFS_NAME = "auth"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "userId"
    private const val KEY_EMAIL = "email"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_USER_PHONE = "userPhone"
    private const val KEY_USER_BIO = "userBio"
    private const val KEY_USER_GENDER = "userGender"

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

    fun getUserName(context: Context): String = prefs(context).getString(KEY_USER_NAME, "") ?: ""
    fun setUserName(context: Context, name: String) {
        prefs(context).edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserPhone(context: Context): String = prefs(context).getString(KEY_USER_PHONE, "") ?: ""
    fun setUserPhone(context: Context, phone: String) {
        prefs(context).edit().putString(KEY_USER_PHONE, phone).apply()
    }

    fun getUserBio(context: Context): String = prefs(context).getString(KEY_USER_BIO, "") ?: ""
    fun setUserBio(context: Context, bio: String) {
        prefs(context).edit().putString(KEY_USER_BIO, bio).apply()
    }

    fun getUserGender(context: Context): String = prefs(context).getString(KEY_USER_GENDER, "") ?: ""
    fun setUserGender(context: Context, gender: String) {
        prefs(context).edit().putString(KEY_USER_GENDER, gender).apply()
    }

    fun logout(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun saveLocalPassword(context: Context, email: String, password: String) {
        context.getSharedPreferences("local_auth", Context.MODE_PRIVATE).edit()
            .putString("pw_$email", password)
            .apply()
    }

    fun checkLocalPassword(context: Context, email: String, password: String): Boolean {
        val stored = context.getSharedPreferences("local_auth", Context.MODE_PRIVATE)
            .getString("pw_$email", null) ?: return false
        return stored == password
    }

    fun isEmailRegistered(context: Context, email: String): Boolean =
        context.getSharedPreferences("local_auth", Context.MODE_PRIVATE).contains("pw_$email")

    fun getStoredPassword(context: Context, email: String): String? =
        context.getSharedPreferences("local_auth", Context.MODE_PRIVATE).getString("pw_$email", null)

    fun saveLocalAvatar(context: Context, base64: String) {
        prefs(context).edit().putString("localAvatar", base64).apply()
    }

    fun getLocalAvatar(context: Context): String = prefs(context).getString("localAvatar", "") ?: ""

    fun saveSecurityQA(context: Context, email: String, question: String, answer: String) {
        context.getSharedPreferences("security_qa", Context.MODE_PRIVATE).edit()
            .putString("q_$email", question)
            .putString("a_$email", answer.lowercase().trim())
            .apply()
    }

    fun getSecurityQuestion(context: Context, email: String): String =
        context.getSharedPreferences("security_qa", Context.MODE_PRIVATE)
            .getString("q_$email", "") ?: ""

    fun checkSecurityAnswer(context: Context, email: String, answer: String): Boolean {
        val stored = context.getSharedPreferences("security_qa", Context.MODE_PRIVATE)
            .getString("a_$email", null) ?: return false
        return stored == answer.lowercase().trim()
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
