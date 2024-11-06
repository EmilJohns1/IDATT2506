package com.example.oving3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FriendsListActivity : AppCompatActivity() {

    private lateinit var friendAdapter: FriendAdapter
    private lateinit var friendsList: MutableList<Friend>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_list)

        friendsList = intent.serializable("friends_list") as? MutableList<Friend> ?: mutableListOf()

        friendAdapter = FriendAdapter(friendsList)
        val recyclerViewFriends: RecyclerView = findViewById(R.id.recyclerViewFriends)


        recyclerViewFriends.layoutManager = LinearLayoutManager(this)
        recyclerViewFriends.adapter = friendAdapter
    }
}
