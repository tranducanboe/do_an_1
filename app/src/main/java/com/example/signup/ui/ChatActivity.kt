package com.example.signup.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.signup.R
import com.example.signup.data.adapter.MessageAdapter
import com.example.signup.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val messages = mutableListOf<String>() // Danh sách tin nhắn
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ẩn thanh trạng thái và thanh điều hướng
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )

        // Nhận dữ liệu từ Intent
        val userName = intent.getStringExtra("USER_NAME") ?: "User"

        // Gán tên người dùng vào TextView
        binding.textName.text = userName

        // Cài đặt giao diện và xử lý sự kiện
        setupUI()
    }

    private fun setupUI() {
        // Thiết lập RecyclerView
        adapter = MessageAdapter(messages)
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.adapter = adapter

        // Xử lý nút quay lại
        binding.imgBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // Xử lý nút thêm
        binding.imgAdd.setOnClickListener {
            // Ví dụ: thêm file hoặc hình ảnh
        }

        // Xử lý nút gửi tin nhắn
        binding.imgSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString()
            if (message.isNotBlank()) {
                sendMessage(message)
            }
        }
    }

    private fun sendMessage(message: String) {
        messages.add(message) // Thêm tin nhắn vào danh sách
        adapter.notifyItemInserted(messages.size - 1) // Cập nhật RecyclerView
        binding.recyclerViewMessages.scrollToPosition(messages.size - 1) // Cuộn xuống cuối
        binding.editTextMessage.text.clear() // Xóa nội dung trong EditText
    }
}
