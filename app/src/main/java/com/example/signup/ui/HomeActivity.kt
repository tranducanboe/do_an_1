package com.example.signup.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.signup.R
import com.example.signup.data.api.RetrofitClient
import com.example.signup.data.model.User
import com.example.signup.data.repository.UserRepository
import com.example.signup.databinding.ActivityHomeBinding
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var isVideoServiceInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )

        setupUI()

        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val currentUser = sharedPreferencesHelper.getUser()

        if (currentUser != null) {
            binding.txtName.text = currentUser.name
            // Khởi tạo dịch vụ cuộc gọi ngay khi có thông tin user
            if (currentUser.id != null) {
                videoCallServices(currentUser.id)
            } else {
                Toast.makeText(this, "Không thể khởi tạo dịch vụ cuộc gọi: ID người dùng không hợp lệ", Toast.LENGTH_LONG).show()
            }
            loadUsers(currentUser)
        } else {
            navigateToLogin()
        }
    }

    private fun setupUI() {
        binding.imgSetting.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            val menu = popupMenu.menu
            menu.add("Đăng xuất")

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Đăng xuất" -> {
                        Log.d("Settings", "Đăng xuất")
                        SharedPreferencesHelper(this).clearUser()
                        // Hủy dịch vụ cuộc gọi khi đăng xuất
                        ZegoUIKitPrebuiltCallInvitationService.unInit()
                        isVideoServiceInitialized = false

                        val intentLogout = Intent(this, LoginActivity::class.java)
                        startActivity(intentLogout)
                        finish()
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun loadUsers(currentUser: User) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val apiService = RetrofitClient.getApiService()
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = userRepository.getUsers()
                val filteredUsers = users.filter { it.email != currentUser.email }

                withContext(Dispatchers.Main) {
                    recyclerView.adapter = UserAdapter(filteredUsers,
                        onUserClick = { user ->
                            val intent = Intent(this@HomeActivity, ChatActivity::class.java)
                            intent.putExtra("USER_NAME", user.name)
                            intent.putExtra("EMAIL", user.email)
                            startActivity(intent)
                        },
                        onCallClick = { user ->
                            if (!isVideoServiceInitialized) {
                                Toast.makeText(this@HomeActivity,
                                    "Đang khởi tạo dịch vụ cuộc gọi, vui lòng thử lại",
                                    Toast.LENGTH_SHORT).show()
                                return@UserAdapter
                            }

                            if (user.id != null) {
                                val intent = Intent(this@HomeActivity, CallActivity::class.java)
                                intent.putExtra("USER_NAME", user.name)
                                intent.putExtra("userID", user.id)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@HomeActivity,
                                    "Không thể thực hiện cuộc gọi: ID người dùng không hợp lệ",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity,
                        "Không thể tải danh sách người dùng: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun videoCallServices(userId: String) {
        try {
            val appID: Long = 782024042
            val appSign = "0fdd27e41817b6d1a1d63adddd700d26f9bff7177eeedba79c2e37ce9a935a68"

            val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()

            val notificationConfig = ZegoNotificationConfig().apply {
                sound = "zego_uikit_sound_call"
                channelID = "CallInvitation"
                channelName = "CallInvitation"
            }

            callInvitationConfig.notificationConfig = notificationConfig

            ZegoUIKitPrebuiltCallInvitationService.init(
                application,
                appID,
                appSign,
                userId,
                userId,
                callInvitationConfig
            )

            isVideoServiceInitialized = true
            Log.d("VideoCall", "Khởi tạo dịch vụ cuộc gọi thành công cho userId: $userId")
        } catch (e: Exception) {
            Log.e("VideoCall", "Lỗi khởi tạo dịch vụ cuộc gọi: ${e.message}")
            Toast.makeText(this,
                "Không thể khởi tạo dịch vụ cuộc gọi: ${e.message}",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isVideoServiceInitialized) {
            ZegoUIKitPrebuiltCallInvitationService.unInit()
        }
    }
}
