package com.example.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signup.data.model.Message
import com.example.signup.data.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            repository.sendMessage(message)
        }
    }

    fun loadMessages(chatPartnerId: String) {
        viewModelScope.launch {
            val loadedMessages = repository.getMessages(chatPartnerId)
            _messages.value = loadedMessages
        }
    }
}