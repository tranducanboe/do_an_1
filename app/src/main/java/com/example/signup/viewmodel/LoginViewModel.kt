package com.example.signup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signup.data.model.User
import com.example.signup.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>> = _userList

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _error.value = "Email và mật khẩu không thể để trống"
            return
        }

        viewModelScope.launch {
            try {
                val user = userRepository.getUserByEmailAndPassword(email, password)

                if (user != null) {
                    _loginSuccess.value = true
                    // Tự động tải danh sách người dùng sau khi đăng nhập
                    fetchUserList()
                } else {
                    _error.value = "Đăng nhập thất bại, email hoặc mật khẩu sai"
                }
            } catch (e: Exception) {
                Log.e("1dsa", "Lỗi đăng nhập: ${e.message}", e)
                _error.value = "Đăng nhập thất bại: ${e.message}"
            }
        }
    }

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