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

    // Thực hiện việc đăng ký người dùng mới và trả về phản hồi từ server.
    suspend fun registerUser(user: User): Response<User> {
        return apiService.registerUser(user)
    }

    // Lấy danh sách người dùng, loại trừ người dùng hiện tại
    suspend fun getUsers(): List<User> {
        val users = apiService.getUsers()
        return users.filter { it.email != currentUser?.email }
    }

    // Đăng nhập và lưu người dùng hiện tại
    suspend fun getUserByEmailAndPassword(email: String, password: String): User? {
        val response = apiService.login(mapOf("email" to email, "password" to password))
        return if (response.isSuccessful) {
            currentUser = response.body()
            currentUser
        } else {
            null
        }
    }

    // Phương thức để lấy người dùng hiện tại (nếu cần)
    fun getCurrentUser(): User? = currentUser
}