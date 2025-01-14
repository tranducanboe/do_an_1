package com.example.signup.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.signup.R
import com.example.signup.data.model.User

class UserAdapter(
    private val users: List<User>,
    private val onUserClick: (User) -> Unit,
    private val onCallClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.name)
        val callImageView: View = view.findViewById(R.id.img_call)

        init {
            itemView.setOnClickListener {
                onUserClick(users[adapterPosition])
            }
            callImageView.setOnClickListener {
                onCallClick(users[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.nameTextView.text = user.name
    }

    override fun getItemCount(): Int = users.size
}
