package com.example.signup.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        initializeViews()
        checkPermissionsAndSetup()
    }

    private fun initializeViews() {
        try {
            textView = findViewById(R.id.call_info_text_view)
            videoCallBtn = findViewById(R.id.video_call_btn)
            audioCallBtn = findViewById(R.id.audio_call_btn)
            buttonLayout = findViewById(R.id.buttons_layout)
        } catch (e: Exception) {
            Log.e("CallActivity", "Error initializing views: ${e.message}")
            Toast.makeText(this, "Lỗi khởi tạo giao diện", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun checkPermissionsAndSetup() {
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            setupCall()
        } else {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun setupCall() {
        val userId = intent.getStringExtra("userID")
        val userName = intent.getStringExtra("USER_NAME")

        if (userId == null || userName == null) {
            Log.e("CallActivity", "Missing user information")
            Toast.makeText(this, "Thiếu thông tin người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        try {
            textView.text = "Đang gọi cho $userName"
            buttonLayout.visibility = View.VISIBLE

            // Setup video call button
            videoCallBtn.apply {
                setIsVideoCall(true)
                resourceID = "zego_uikit_call"
                setInvitees(listOf(ZegoUIKitUser(userId)))
            }

            // Setup audio call button
            audioCallBtn.apply {
                setIsVideoCall(false)
                resourceID = "zego_uikit_call"
                setInvitees(listOf(ZegoUIKitUser(userId)))
            }

        } catch (e: Exception) {
            Log.e("CallActivity", "Error setting up call: ${e.message}")
            Toast.makeText(this, "Lỗi thiết lập cuộc gọi", Toast.LENGTH_SHORT).show()
            finish()
        }
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
                    "Cần cấp đầy đủ quyền để thực hiện cuộc gọi",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}