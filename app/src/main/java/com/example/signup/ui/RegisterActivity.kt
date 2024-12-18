package com.example.signup.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.signup.R
import com.example.signup.data.api.RetrofitClient
import com.example.signup.data.repository.UserRepository
import com.example.signup.viewmodel.RegistrationViewModel
import com.example.signup.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )


        // Khởi tạo ApiService và UserRepository
        val apiService = RetrofitClient.getApiService()
        val userRepository = UserRepository(apiService)
        viewModel = ViewModelProvider(this, ViewModelFactory(userRepository)).get(RegistrationViewModel::class.java)

        // Khai báo các view
        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<TextInputEditText>(R.id.confirmPasswordInput)
        val emailError = findViewById<TextView>(R.id.emailError)
        val passwordError = findViewById<TextView>(R.id.passwordError)
        val confirmPasswordError = findViewById<TextView>(R.id.confirmPasswordError)
        val btnRegister = findViewById<Button>(R.id.btnLogin)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val txtLoginLink = findViewById<TextView>(R.id.txtLoginLink)

        btnBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        txtLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Quan sát các lỗi từ ViewModel
        viewModel.emailError.observe(this) { error ->
            emailError.text = error
            emailError.visibility = if (error != null) View.VISIBLE else View.GONE
        }

        viewModel.passwordError.observe(this) { error ->
            passwordError.text = error
            passwordError.visibility = if (error != null) View.VISIBLE else View.GONE
        }

        viewModel.confirmPasswordError.observe(this) { error ->
            confirmPasswordError.text = error
            confirmPasswordError.visibility = if (error != null) View.VISIBLE else View.GONE
        }


        viewModel.registerSuccess.observe(this) { success ->
            if (success) {
                // Lấy id người dùng
                val userId = viewModel.userId.value
                // Chuyển đến màn hình chính hoặc hiển thị thông báo thành công
                Toast.makeText(this, "Đăng ký thành công! ID: $userId", Toast.LENGTH_SHORT).show()
            }
        }

        // Thiết lập sự kiện khi nút đăng ký được nhấn
        btnRegister.setOnClickListener {
            val name = nameInput.text.toString().trim()  // Lấy giá trị tên
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()
            viewModel.validateInputs(email, password, confirmPassword, name)
        }
    }
}
