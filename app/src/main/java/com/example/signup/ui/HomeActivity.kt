package com.example.signup.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.signup.R
import com.example.signup.data.api.RetrofitClient
import com.example.signup.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch users from API
        val apiService = RetrofitClient.getApiService()
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            val users = userRepository.getUsers() // API call
            withContext(Dispatchers.Main) {
                recyclerView.adapter = UserAdapter(users){user ->
                    val intent = Intent(this@HomeActivity, ChatActivity::class.java)
                    intent.putExtra("USER_NAME", user.name)
                    startActivity(intent)
                }
            }
        }
    }
}