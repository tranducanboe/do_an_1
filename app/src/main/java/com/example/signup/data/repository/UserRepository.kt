package com.example.signup.data.repository

import com.example.signup.data.api.ApiService
import com.example.signup.data.model.User
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {
    private var currentUser: User? = null

    // Kiểm tra xem một email có tồn tại hay không
    suspend fun checkEmailExists(email: String): Boolean {
        val response = apiService.checkEmailExists(email)
        return response.isSuccessful && response.body()?.isNotEmpty() == true
    }

    // Đăng ký người dùng mới
    suspend fun registerUser(user: User): Response<User> {
        return apiService.registerUser(user)
    }

    // Lấy danh sách người dùng
    suspend fun getUsers(): List<User> {
        val response = apiService.getUsers()
        return response
    }

    // Đăng nhập bằng email và mật khẩu
    suspend fun getUserByEmailAndPassword(email: String, password: String): User? {
        val response = apiService.checkEmailExists(email)
        if (response.isSuccessful) {
            val users = response.body()
            val user = users?.find { it.password == password }
            if (user != null) {
                currentUser = user
                return user
            }
        }
        return null
    }
}
