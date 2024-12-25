package com.example.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signup.data.model.User
import com.example.signup.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError

    private val _nameError = MutableLiveData<String?>()  // Thêm lỗi cho tên
    val nameError: LiveData<String?> = _nameError

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> = _userId

    fun validateInputs(email: String, password: String, confirmPassword: String, name: String) {
        var isValid = true

        // Kiểm tra email
        _emailError.value = if (!isValidEmail(email)) "Email không hợp lệ" else null

        // Kiểm tra mật khẩu
        _passwordError.value = if (password.length < 8) "Mật khẩu phải có ít nhất 8 ký tự" else null

        // Kiểm tra xác nhận mật khẩu
        _confirmPasswordError.value = if (password != confirmPassword) "Mật khẩu không khớp" else null

        // Kiểm tra tên
        _nameError.value = if (name.isBlank()) "Tên không được bỏ trống" else null

        // Nếu không có lỗi nào, tiến hành gọi hàm đăng ký
        if (_emailError.value == null && _passwordError.value == null && _confirmPasswordError.value == null && _nameError.value == null) {
            register(email, password, name)
        } else {
            isValid = false
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            // Kiểm tra email đã tồn tại
            if (userRepository.checkEmailExists(email)) {
                _emailError.value = "Email đã tồn tại"
            } else if (name.isBlank()) { // Kiểm tra tên có hợp lệ không
                _nameError.value = "Tên không được để trống" // Hiển thị lỗi tên
            } else {
                val user = User(id = null, email = email, password = password, name = name, imageUrl = "") // Thêm imageUrl nếu cần
                val response = userRepository.registerUser(user)
                if (response.isSuccessful) {
                    _userId.value = response.body()?.id // Lưu id của người dùng
                    _registerSuccess.value = true
                }
            }
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
