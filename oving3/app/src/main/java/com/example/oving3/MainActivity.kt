package com.example.oving3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val friendsList = mutableListOf<Friend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonAddFriend: Button = findViewById(R.id.buttonAddFriend)
        val buttonShowFriends: Button = findViewById(R.id.buttonShowFriends)
        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextDOB: EditText = findViewById(R.id.editTextDOB)

        buttonAddFriend.setOnClickListener {
            val name = editTextName.text.toString()
            val dob = editTextDOB.text.toString()
            if (name.isNotEmpty() && dob.isNotEmpty()) {
                friendsList.add(Friend(name, dob))
                editTextName.text.clear()
                editTextDOB.text.clear()
            }
        }

        buttonShowFriends.setOnClickListener {
            val intent = Intent(this, FriendsListActivity::class.java)
            intent.putExtra("friends_list", ArrayList(friendsList))
            startActivity(intent)
        }
    }
}
