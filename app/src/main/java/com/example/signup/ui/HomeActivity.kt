package com.example.signup.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    companion object {
        const val APP_ID: Long = 782024042L  // Your Zego App ID
        const val APP_SIGN = "0fdd27e41817b6d1a1d63adddd700d26f9bff7177eeedba79c2e37ce9a935a68"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        // Lấy thông tin người dùng từ SharedPreferences
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val currentUser = sharedPreferencesHelper.getUser()

        if (currentUser != null) {
            binding.txtName.text = currentUser.name
            Glide.with(this).load(currentUser.imageUrl).into(binding.imageView3)
            initVideoCallService(currentUser.id)
            loadUsers(currentUser)
        } else {
            navigateToLogin()
        }
    }

    private fun initVideoCallService(userId: String?) {
        if (userId == null) {
            Log.e("VideoCall", "User ID is null")
            return
        }

        try {
            // Tạo config cho cuộc gọi
            val callConfig = ZegoUIKitPrebuiltCallInvitationConfig().apply {
                notificationConfig = ZegoNotificationConfig().apply {
                    sound = "zego_uikit_sound_call"
                    channelID = "CallInvitation"
                    channelName = "CallInvitation"
                }
            }

            // Khởi tạo service
            ZegoUIKitPrebuiltCallInvitationService.init(
                application,
                APP_ID,
                APP_SIGN,
                userId,
                userId,
                callConfig
            )

            isVideoServiceInitialized = true
            Log.d("VideoCall", "Video call service initialized successfully")

        } catch (e: Exception) {
            Log.e("VideoCall", "Failed to initialize video call service: ${e.message}")
            e.printStackTrace()

            // Thử khởi tạo lại sau 2 giây nếu thất bại
            binding.root.postDelayed({
                if (!isVideoServiceInitialized) {
                    initVideoCallService(userId)
                }
            }, 2000)
        }
    }

    private fun setupUI() {
        binding.imgSetting.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            val menu = popupMenu.menu
            menu.add("Cập nhật thông tin")
            menu.add("Đăng xuất")

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Cập nhật thông tin" -> {
                        startActivity(Intent(this, UpdateProfileActivity::class.java))
                    }
                    "Đăng xuất" -> {
                        try {
                            if (isVideoServiceInitialized) {
                                ZegoUIKitPrebuiltCallInvitationService.unInit()
                            }
                        } catch (e: Exception) {
                            Log.e("VideoCall", "Error uninitializing service: ${e.message}")
                        }

                        SharedPreferencesHelper(this).clearUser()
                        startActivity(Intent(this, LoginActivity::class.java))
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

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = RetrofitClient.getApiService()
                    .let { UserRepository(it).getUsers() }
                    .filter { it.email != currentUser.email }

                withContext(Dispatchers.Main) {
                    recyclerView.adapter = UserAdapter(users,
                        onUserClick = { user ->
                            startActivity(Intent(this@HomeActivity, ChatActivity::class.java).apply {
                                putExtra("USER_NAME", user.name)
                                putExtra("EMAIL", user.email)
                            })
                        },
                        onCallClick = { user ->
                            if (!isVideoServiceInitialized) {
                                Toast.makeText(this@HomeActivity,
                                    "Đang khởi tạo dịch vụ cuộc gọi, vui lòng đợi",
                                    Toast.LENGTH_SHORT).show()
                                // Thử khởi tạo lại service
                                initVideoCallService(currentUser.id)
                                return@UserAdapter
                            }

                            if (user.id != null) {
                                startActivity(Intent(this@HomeActivity, CallActivity::class.java).apply {
                                    putExtra("USER_NAME", user.name)
                                    putExtra("userID", user.id)
                                })
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

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    override fun onResume() {
        super.onResume()

        // Lấy thông tin người dùng từ SharedPreferences
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val currentUser = sharedPreferencesHelper.getUser()

        if (currentUser != null) {
            // Hiển thị tên, email, mật khẩu và ảnh đại diện
            binding.txtName.text = currentUser.name
            Glide.with(this).load(currentUser.imageUrl).into(binding.imageView3)
            initVideoCallService(currentUser.id)
            loadUsers(currentUser)
        } else {
            navigateToLogin()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (isVideoServiceInitialized) {
                ZegoUIKitPrebuiltCallInvitationService.unInit()
            }
        } catch (e: Exception) {
            Log.e("VideoCall", "Error in onDestroy: ${e.message}")
        }
    }
}
