package com.example.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signup.data.model.User
import com.example.signup.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginSuccess = MutableLiveData<User?>()
    val loginSuccess: LiveData<User?> = _loginSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>> = _userList

    // Đăng nhập
    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _error.value = "Email và mật khẩu không thể để trống"
            return
        }

        viewModelScope.launch {
            try {
                val user = userRepository.getUserByEmailAndPassword(email, password)
                if (user != null) {
                    _loginSuccess.value = user
                    fetchUserList() // Load danh sách người dùng sau khi đăng nhập
                } else {
                    _error.value = "Đăng nhập thất bại, email hoặc mật khẩu không chính xác"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi xảy ra khi đăng nhập: ${e.message}"
            }
        }
    }

    // Lấy danh sách người dùng
    private fun fetchUserList() {
        viewModelScope.launch {
            try {
                val users = userRepository.getUsers()
                _userList.value = users
            } catch (e: Exception) {
                _error.value = "Không thể tải danh sách người dùng: ${e.message}"
            }
        }
    }
}
