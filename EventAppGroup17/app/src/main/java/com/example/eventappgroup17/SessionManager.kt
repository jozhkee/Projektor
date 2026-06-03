package com.example.eventappgroup17

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_ADMIN = "is_admin"
    }

    fun saveSession(token: String, userId: Int, name: String, email: String, isAdmin: Boolean) {
        prefs.edit {
            putString(KEY_TOKEN, token)
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .putBoolean(KEY_IS_ADMIN, isAdmin)
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getIsAdmin(): Boolean = prefs.getBoolean(KEY_IS_ADMIN, false)

    fun clearSession() {
        prefs.edit { clear() }
    }

    fun hasSession(): Boolean = getToken() != null
}