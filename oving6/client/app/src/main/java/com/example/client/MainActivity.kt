package com.example.client

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MainActivity : ComponentActivity() {
    private lateinit var messageLogTextView: TextView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private var socket: Socket? = null
    private val serverIp = "10.0.2.2"
    private val serverPort = 12345
    private val lock = Any()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageLogTextView = findViewById(R.id.tv_client_message_log)
        messageInput = findViewById(R.id.et_message_input)
        sendButton = findViewById(R.id.btn_send_message)

        sendButton.setOnClickListener {
            sendMessage()
        }

        connectToServer()
    }

    private fun connectToServer() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socket = Socket(serverIp, serverPort)
                addMessage("Connected to server at $serverIp:$serverPort")
                readMessagesFromServer(socket)
            } catch (e: Exception) {
                addMessage("Error connecting to server: ${e.message}")
            }
        }
    }

    private fun sendMessage() {
        val message = messageInput.text.toString()
        if (message.isNotBlank()) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    sendMessageToServer(message)
                    addMessage("You: $message")
                    messageInput.text.clear()
                } catch (e: Exception) {
                    addMessage("Error sending message: ${e.message}")
                }
            }
        }
    }

    private suspend fun sendMessageToServer(message: String) {
        socket?.let {
            val writer = PrintWriter(it.getOutputStream(), true)
            writer.println(message)
        }
    }

    private suspend fun readMessagesFromServer(socket: Socket?) {
        socket?.let {
            val reader = BufferedReader(InputStreamReader(it.getInputStream()))
            var message: String?
            while (true) {
                message = reader.readLine() ?: break
                addMessage("$message")
            }
            addMessage("Disconnected from server.")
            it.close()
        }
    }

    private fun addMessage(message: String) {
        synchronized(lock) {
            runOnUiThread {
                messageLogTextView.append("$message\n")
            }
        }
    }
}
