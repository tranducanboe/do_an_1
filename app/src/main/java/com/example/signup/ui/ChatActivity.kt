package com.example.signup.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.signup.data.adapter.ChatAdapter
import com.example.signup.data.model.User
import com.example.signup.databinding.ActivityChatBinding
import com.example.signup.utils.AppUtils

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private var userName = ""
    private var emailReceiver = ""
    private var currentUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
         userName = intent.getStringExtra("USER_NAME") ?: "User"
         emailReceiver = intent.getStringExtra("EMAIL") ?: "email"
         currentUser = SharedPreferencesHelper(this).getUser()
         binding.textName.text = userName
        setupUI()
    }

    private fun setupUI() {
        adapter = currentUser?.let { ChatAdapter(currentUserId = it.email) }!!
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.adapter = adapter
        currentUser?.let {
            AppUtils.loadMessages(currentUserId = it.email, selectedUserId = emailReceiver) { messages ->
                adapter.submitList(messages)
                binding.recyclerViewMessages.scrollToPosition(adapter.itemCount - 1)
            }
        }

        binding.imgBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.imgAdd.setOnClickListener {
        }

        binding.imgSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString()
            if (message.isNotBlank()) {
                sendMessage(message)
            }
        }
    }

    private fun sendMessage(message: String) {
        currentUser?.let {
            AppUtils.sendMessage(
                senderId = it.email,
                receiverId = emailReceiver,
                content = message
            )
        }
        binding.recyclerViewMessages.scrollToPosition(adapter.itemCount - 1)
        binding.editTextMessage.text.clear()
    }
}
