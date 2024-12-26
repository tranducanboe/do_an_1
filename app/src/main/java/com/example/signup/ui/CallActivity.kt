package com.example.signup.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.signup.R
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser

class CallActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var videoCallBtn: ZegoSendCallInvitationButton
    private lateinit var audioCallBtn: ZegoSendCallInvitationButton
    private lateinit var buttonLayout: LinearLayout

    private val PERMISSION_REQUEST_CODE = 1
    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.MODIFY_AUDIO_SETTINGS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_call)

        initializeViews()

        if (checkAndRequestPermissions()) {
            setupCall()
        }

        setFullscreenMode()
    }

    private fun initializeViews() {
        textView = findViewById(R.id.call_info_text_view)
        videoCallBtn = findViewById(R.id.video_call_btn)
        audioCallBtn = findViewById(R.id.audio_call_btn)
        buttonLayout = findViewById(R.id.buttons_layout)
    }

    private fun setupCall() {
        val userId = intent.getStringExtra("userID")
        val receiverName = intent.getStringExtra("USER_NAME")

        if (userId != null) {
            try {
                Log.d("CallActivity", "Thiết lập cuộc gọi cho user: $userId")
                textView.text = "Đang gọi cho $receiverName!"
                buttonLayout.visibility = View.VISIBLE
                setupCallButtons(userId)
            } catch (e: Exception) {
                Log.e("CallActivity", "Lỗi khi thiết lập cuộc gọi: ${e.message}")
                Toast.makeText(this,
                    "Không thể thiết lập cuộc gọi: ${e.message}",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Log.e("CallActivity", "userID là null")
            textView.text = "Không thể xác định người nhận cuộc gọi!"
            buttonLayout.visibility = View.GONE
            Toast.makeText(this,
                "Không thể xác định người nhận cuộc gọi",
                Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val pendingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (pendingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                pendingPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                setupCall()
            } else {
                Toast.makeText(this,
                    "Cần cấp quyền để thực hiện cuộc gọi",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupCallButtons(receiverId: String) {
        try {
            videoCallBtn.apply {
                setIsVideoCall(true)
                resourceID = "zego_uikit_call"
                setInvitees(listOf(ZegoUIKitUser(receiverId)))
            }

            audioCallBtn.apply {
                setIsVideoCall(false)
                resourceID = "zego_uikit_call"
                setInvitees(listOf(ZegoUIKitUser(receiverId)))
            }

            Log.d("CallActivity", "Thiết lập nút gọi thành công")
        } catch (e: Exception) {
            Log.e("CallActivity", "Lỗi khi thiết lập nút gọi: ${e.message}")
            Toast.makeText(this,
                "Không thể thiết lập nút gọi: ${e.message}",
                Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setFullscreenMode() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
    }
}