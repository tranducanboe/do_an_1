package com.example.signup.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.signup.R
import com.example.signup.data.api.RetrofitClient
import com.example.signup.data.repository.UserRepository
import com.example.signup.viewmodel.LoginViewModel
import com.example.signup.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kiểm tra xem user đã đăng nhập hay chưa
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val currentUser = sharedPreferencesHelper.getUser()

        if (currentUser != null) {
            // Nếu đã có user -> Tự động đăng nhập và chuyển đến HomeActivity
            navigateToHome()
            return // Không cần hiển thị màn hình đăng nhập
        }

        setContentView(R.layout.activity_login)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )


        val factory = ViewModelFactory(UserRepository(RetrofitClient.getApiService()))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtSignUpLink = findViewById<TextView>(R.id.txtSignUpLink)

        txtSignUpLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        viewModel.loginSuccess.observe(this) { user ->
            if (user != null) {
                // Lưu user vào SharedPreferences
                sharedPreferencesHelper.saveUser(user)

                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
