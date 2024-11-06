package com.example.server

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class MainActivity : ComponentActivity() {
    private lateinit var messageLogTextView: TextView
    private val PORT = 12345
    private val connectedSockets = mutableListOf<Socket>()
    private val clientIds = mutableMapOf<Socket, String>()
    private val lock = Any()
    private var clientCounter = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageLogTextView = findViewById(R.id.tv_host_message_log)

        startServer()
    }

    private fun startServer() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val serverSocket = ServerSocket(PORT)
                addMessage("Server started, waiting for clients...")

                while (true) {
                    val clientSocket = serverSocket.accept()
                    connectedSockets.add(clientSocket)

                    val clientId = "Client $clientCounter"
                    clientIds[clientSocket] = clientId

                    addMessage("$clientId connected: $clientSocket")
                    handleNewClient(clientSocket, clientCounter)

                    clientCounter++
                }
            } catch (e: Exception) {
                addMessage("Error: ${e.message}")
            }
        }
    }

    private fun addMessage(message: String) {
        synchronized(lock) {
            runOnUiThread {
                messageLogTextView.append("$message\n")
            }
        }
    }

    private fun handleNewClient(clientSocket: Socket, clientNumber: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val welcomeMessage = "Welcome to the server, client$clientNumber!"
                sendMessageToClient(clientSocket, welcomeMessage) // Send welcome message
                readMessagesFromClient(clientSocket) // Start reading messages
            } catch (e: Exception) {
                addMessage("Error handling client: ${e.message}")
            }
        }
    }

    private suspend fun readMessagesFromClient(socket: Socket) {
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        var message: String?
        while (true) {
            message = reader.readLine() ?: break
            val clientId = clientIds[socket] ?: "Unknown"
            addMessage("$clientId says: $message")
            forwardMessageToClients(socket, "$clientId: $message")
        }
        connectedSockets.remove(socket)
        clientIds.remove(socket)
        socket.close()
    }

    private suspend fun forwardMessageToClients(sourceSocket: Socket, message: String) {
        for (socket in connectedSockets) {
            if (socket != sourceSocket) {
                sendMessageToClient(socket, message)
            }
        }
    }

    private suspend fun sendMessageToClient(socket: Socket, message: String) {
        val writer = PrintWriter(socket.getOutputStream(), true)
        writer.println(message)
        addMessage("Sent to client: $message")
    }
}
