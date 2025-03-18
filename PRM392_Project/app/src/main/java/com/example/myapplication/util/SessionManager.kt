package com.example.myapplication.util

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.model.User

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    
    companion object {
        private const val PREF_NAME = "BookstoreAppSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_ADMIN = "isAdmin"
    }
    
    fun createLoginSession(user: User) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_USER_ID, user.id)
        editor.putString(KEY_USERNAME, user.username)
        editor.putBoolean(KEY_IS_ADMIN, user.isAdmin)
        editor.apply()
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun isAdmin(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_ADMIN, false)
    }
    
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }
    
    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }
    
    fun getUserDetails(): User? {
        if (!isLoggedIn()) {
            return null
        }
        
        val userId = getUserId()
        val username = getUsername() ?: return null
        
        return User(
            id = userId,
            username = username,
            password = "", // Password is not stored in session
            isAdmin = isAdmin()
        )
    }
    
    fun logout() {
        editor.clear()
        editor.apply()
    }
} 