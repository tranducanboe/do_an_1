package com.example.signup.ui

import android.content.Context
import android.content.SharedPreferences
import com.example.signup.data.model.User
import com.google.gson.Gson

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    // Lưu thông tin người dùng vào SharedPreferences
    fun saveUser(user: User?) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val userJson = gson.toJson(user)
        editor.putString("USER", userJson)  // Lưu đối tượng User dưới dạng JSON
        editor.putString("EMAIL", user?.email)  // Lưu email vào SharedPreferences
        editor.putString("PASSWORD", user?.password)  // Lưu mật khẩu vào SharedPreferences
        editor.putString("IMAGE_URL", user?.imageUrl)  // Lưu ảnh đại diện (URL hoặc đường dẫn ảnh)
        editor.apply()
    }

    // Lấy thông tin người dùng từ SharedPreferences
    fun getUser(): User? {
        val gson = Gson()
        val userJson = sharedPreferences.getString("USER", null)
        return if (!userJson.isNullOrEmpty()) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null // Trả về null nếu không có dữ liệu
        }
    }


    // Lấy email của người dùng từ SharedPreferences
    fun getEmail(): String? {
        return sharedPreferences.getString("EMAIL", null)
    }

    // Lấy mật khẩu của người dùng từ SharedPreferences
    fun getPassword(): String? {
        return sharedPreferences.getString("PASSWORD", null)
    }

    // Lấy URL ảnh đại diện của người dùng từ SharedPreferences
    fun getImageUrl(): String? {
        return sharedPreferences.getString("IMAGE_URL", null)
    }

    // Xóa thông tin người dùng khỏi SharedPreferences
    fun clearUser() {
        val editor = sharedPreferences.edit()
        editor.remove("USER")
        editor.remove("EMAIL")
        editor.remove("PASSWORD")
        editor.remove("IMAGE_URL")
        editor.apply()
    }
}
