package com.example.signup.ui

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.signup.data.model.User
import com.example.signup.databinding.ActivityUpdateProfileBinding

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hiển thị thông tin người dùng hiện tại
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val currentUser = sharedPreferencesHelper.getUser()

        if (currentUser != null) {
            binding.edtName.setText(currentUser.name)
            binding.edtEmail.setText(currentUser.email)
            binding.edtPassword.setText(currentUser.password)
            Glide.with(this).load(currentUser.imageUrl).into(binding.imgProfile)
        }

        // Xử lý chọn ảnh khi nhấn vào button "Chọn ảnh đại diện"
        binding.btnChangeProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Lưu thông tin khi nhấn nút "Lưu"
        binding.btnSave.setOnClickListener {
            // Lấy thông tin từ các EditText và ImageView
            val updatedName = binding.edtName.text.toString()
            val updatedEmail = binding.edtEmail.text.toString()
            val updatedPassword = binding.edtPassword.text.toString()
// Lấy URI của ảnh đã chọn (có thể là Uri hoặc đường dẫn của ảnh)
            val updatedImageUri = binding.imgProfile.tag?.toString() ?: ""

// Nếu updatedImageUri là null hoặc rỗng, bạn có thể cung cấp giá trị mặc định
            val imageUrl = if (updatedImageUri.isNotEmpty()) {
                updatedImageUri
            } else {
                "default_image_url" // Thay thế bằng một URL ảnh mặc định nếu không có ảnh được chọn
            }

// Lưu thông tin người dùng vào SharedPreferences
            val sharedPreferencesHelper = SharedPreferencesHelper(this)
            sharedPreferencesHelper.saveUser(User(id = null, email = updatedEmail, password = updatedPassword, name = updatedName, imageUrl = imageUrl))

            Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show()
            finish()

        }
    }

    // Xử lý kết quả trả về khi người dùng chọn ảnh
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                // Sử dụng Glide để tải ảnh vào ImageView
                Glide.with(this).load(selectedImageUri).into(binding.imgProfile)
                // Lưu URI của ảnh vào tag để sau này sử dụng
                binding.imgProfile.tag = selectedImageUri.toString()
            } else {
                Toast.makeText(this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
