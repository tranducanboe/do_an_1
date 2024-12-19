package com.example.signup.utils

import android.util.Log
import com.example.signup.data.model.Message
import com.example.signup.data.model.MessageType
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object AppUtils {
    private fun createConversationID(email1: String, email2: String): String {
        val normalizedEmail1 = email1.replace(".", "_")
        val normalizedEmail2 = email2.replace(".", "_")
        val sortedEmails = listOf(normalizedEmail1, normalizedEmail2).sorted()
        return "${sortedEmails[0]}_${sortedEmails[1]}"
    }


    fun sendMessage(senderId: String, receiverId: String, content: String, type: MessageType = MessageType.TEXT) {
        val conversationID = createConversationID(senderId, receiverId)
        val database = FirebaseDatabase.getInstance().reference
        val messageID = database.child("messages").child(conversationID).push().key

        if (messageID != null) {
            val message = Message(
                id = messageID,
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                type = type
            )

            database.child("messages")
                .child(conversationID)
                .child(messageID)
                .setValue(message)
                .addOnSuccessListener {
                    Log.d("hoangtv", "Tin nhắn đã được gửi thành công!:")
                }
                .addOnFailureListener {
                    Log.d("hoangtv", "Gửi tin nhắn thất bại: ${it.message}")
                }
        } else {
            Log.d("hoangtv", "Không thể tạo messageID!")
        }
    }


    fun loadMessages(
        currentUserId: String,
        selectedUserId: String,
        onMessagesLoaded: (List<Message>) -> Unit
    ) {
        val conversationID = createConversationID(currentUserId, selectedUserId)
        val database = FirebaseDatabase.getInstance().reference.child("messages").child(conversationID)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                onMessagesLoaded(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Lỗi khi tải tin nhắn: ${error.message}")
            }
        })
    }

}
