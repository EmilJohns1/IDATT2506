package com.example.oving3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendAdapter(
    private val friendList: MutableList<Friend>
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewFriendName)
        val dobTextView: TextView = itemView.findViewById(R.id.textViewFriendDOB)
        val editTextName: EditText = itemView.findViewById(R.id.editTextFriendName)
        val editTextDOB: EditText = itemView.findViewById(R.id.editTextFriendDOB)
        val editButton: Button = itemView.findViewById(R.id.editFriendButton)
        val updateButton: Button = itemView.findViewById(R.id.updateFriendButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_list_item, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendList[position]

        holder.nameTextView.text = friend.name
        holder.dobTextView.text = friend.birthday

        holder.editTextName.setText(friend.name)
        holder.editTextDOB.setText(friend.birthday)

        holder.editButton.setOnClickListener {
            holder.nameTextView.visibility = View.GONE
            holder.dobTextView.visibility = View.GONE
            holder.editTextName.visibility = View.VISIBLE
            holder.editTextDOB.visibility = View.VISIBLE
            holder.editButton.visibility = View.GONE
            holder.updateButton.visibility = View.VISIBLE
        }

        holder.updateButton.setOnClickListener {
            friend.name = holder.editTextName.text.toString()
            friend.birthday = holder.editTextDOB.text.toString()

            holder.nameTextView.text = friend.name
            holder.dobTextView.text = friend.birthday

            holder.nameTextView.visibility = View.VISIBLE
            holder.dobTextView.visibility = View.VISIBLE
            holder.editTextName.visibility = View.GONE
            holder.editTextDOB.visibility = View.GONE
            holder.editButton.visibility = View.VISIBLE
            holder.updateButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return friendList.size
    }
}
