package com.example.signup.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.signup.R
import com.example.signup.data.api.RetrofitClient
import com.example.signup.data.model.User
import com.example.signup.data.repository.UserRepository
import com.example.signup.databinding.ActivityHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

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

        // Kiểm tra xem người dùng đã đăng nhập chưa thông qua SharedPreferences
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val currentUser = sharedPreferencesHelper.getUser()
        if (currentUser != null) {
            binding.txtName.text = currentUser.name
        }
        if (currentUser == null) {
            // Nếu không có user, chuyển đến màn hình đăng nhập
            navigateToLogin()
        } else {
            // Nếu có user, tiếp tục load danh sách người dùng
            loadUsers(currentUser)
        }
    }

    private fun setupUI() {
        binding.imgSetting.setOnClickListener { view ->
            // Tạo một PopupMenu để hiển thị các lựa chọn dưới imgSetting
            val popupMenu = PopupMenu(this, view)

            // Thêm các mục vào PopupMenu
            val menu = popupMenu.menu
            menu.add("Cập nhật thông tin")
            menu.add("Đăng xuất")

            // Xử lý sự kiện khi người dùng chọn một mục
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Cập nhật thông tin" -> {
                        Log.d("Settings", "Cập nhật thông tin người dùng")
                        val intent = Intent(this, UpdateProfileActivity::class.java)
                        startActivity(intent)
                    }
                    "Đăng xuất" -> {
                        Log.d("Settings", "Đăng xuất")
                        // Xóa thông tin người dùng từ SharedPreferences
                        SharedPreferencesHelper(this).clearUser()
                        val intentLogout = Intent(this, LoginActivity::class.java)
                        startActivity(intentLogout)
                        finish() // Đảm bảo không quay lại màn hình trước đó
                    }
                }
                true
            }

            // Hiển thị PopupMenu
            popupMenu.show()
        }
    }

    private fun loadUsers(currentUser: User) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch users from API
        val apiService = RetrofitClient.getApiService()
        val userRepository = UserRepository(apiService)

        CoroutineScope(Dispatchers.IO).launch {
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
                        val intent = Intent(this@HomeActivity, CallActivity::class.java)
                        intent.putExtra("USER_NAME", user.name)
                        startActivity(intent)
                    }
                )
            }
        }
    }


    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
