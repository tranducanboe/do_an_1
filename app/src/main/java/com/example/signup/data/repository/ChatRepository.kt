package com.example.signup.data.repository

import com.example.signup.data.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun sendMessage(message: Message) {
        firestore.collection("messages")
            .add(message)
    }

    suspend fun getMessages(chatPartnerId: String): List<Message> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        return firestore.collection("messages")
            .whereIn("senderId", listOf(currentUserId, chatPartnerId))
            .whereIn("receiverId", listOf(currentUserId, chatPartnerId))
            .orderBy("timestamp")
            .get()
            .await()
            .toObjects(Message::class.java)
    }
}