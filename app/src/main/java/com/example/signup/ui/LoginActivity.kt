package com.example.signup.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.signup.R
import com.example.signup.data.api.RetrofitClient
import com.example.signup.data.repository.UserRepository
import com.example.signup.viewmodel.LoginViewModel
import com.example.signup.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    // Sử dụng ViewModelProvider để khởi tạo ViewModel
    private lateinit var viewModel: LoginViewModel

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Khởi tạo ViewModel sử dụng ViewModelProvider
        val factory = ViewModelFactory(UserRepository(RetrofitClient.getApiService()))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE)

        if (isUserLoggedIn()) {
            navigateToHome()
            return
        }

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

            viewModel.login(email, password)
        }

        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                saveUserLoggedInState(true)
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

    private fun saveUserLoggedInState(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("IS_LOGGED_IN", isLoggedIn)
        editor.apply()
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("IS_LOGGED_IN", false)
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}