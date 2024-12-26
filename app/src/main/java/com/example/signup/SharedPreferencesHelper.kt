package com.example.signup.ui

import android.content.Context
import android.content.SharedPreferences
import com.example.signup.data.model.User
import com.google.gson.Gson

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun saveUser(user: User?) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val userJson = gson.toJson(user)
        editor.putString("USER", userJson)
        editor.apply()
    }

    fun getUser(): User? {
        val gson = Gson()
        val userJson = sharedPreferences.getString("USER", null)
        return if (!userJson.isNullOrEmpty()) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    fun clearUser() {
        val editor = sharedPreferences.edit()
        editor.remove("USER")
        editor.apply()
    }
}
