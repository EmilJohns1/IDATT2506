package com.example.oving5

import HTTP
import HttpWrapper
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val URL = "https://bigdata.idi.ntnu.no/mobil/tallspill.jsp"

class MainActivity : AppCompatActivity() {
    private val network: HttpWrapper = HttpWrapper(URL)

    private lateinit var inputNavn: EditText
    private lateinit var inputKortnummer: EditText
    private lateinit var inputTall: EditText
    private lateinit var startSpillButton: Button
    private lateinit var sendGjettButton: Button
    private lateinit var serverResponse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputNavn = findViewById(R.id.inputNavn)
        inputKortnummer = findViewById(R.id.inputKortnummer)
        inputTall = findViewById(R.id.inputTall)
        startSpillButton = findViewById(R.id.startSpillButton)
        sendGjettButton = findViewById(R.id.sendGjettButton)
        serverResponse = findViewById(R.id.serverResponse)

        startSpillButton.setOnClickListener { startNyttSpill() }
        sendGjettButton.setOnClickListener { sendGjett() }

        sendGjettButton.isEnabled = false
    }

    private fun startNyttSpill() {
        val navn = inputNavn.text.toString()
        val kortnummer = inputKortnummer.text.toString()

        if (navn.isEmpty() || kortnummer.isEmpty()) {
            serverResponse.text = "Vennligst fyll ut både navn og kortnummer."
            Log.i("startNyttSpill", "Navn eller kortnummer mangler")
            return
        }

        val parameters = mapOf("navn" to navn, "kortnummer" to kortnummer)
        Log.d("startNyttSpill", "Sender forespørsel for å starte nytt spill med navn: $navn og kortnummer: $kortnummer")
        performRequest(HTTP.GET, parameters) { response ->
            serverResponse.text = response
            Log.d("startNyttSpill", "Mottatt respons fra serveren: $response")
            if (response.contains("Oppgi et tall mellom")) {
                sendGjettButton.isEnabled = true
                startSpillButton.isEnabled = false
            } else {
                sendGjettButton.isEnabled = false
            }
        }
    }

    private fun sendGjett() {
        val tall = inputTall.text.toString()

        if (tall.isEmpty()) {
            serverResponse.text = "Vennligst skriv inn et tall."
            Log.i("sendGjett", "Ingen tall oppgitt for gjett")
            return
        }

        val parameters = mapOf("tall" to tall)
        Log.d("sendGjett", "Sender gjett til serveren med tall: $tall")
        performRequest(HTTP.GET, parameters) { response ->
            serverResponse.text = response
            Log.d("sendGjett", "Respons fra server: $response")

            if (response.contains("du har vunnet")) {
                Log.i("sendGjett", "Brukeren vant!")
                sendGjettButton.isEnabled = false
                startSpillButton.isEnabled = true
            } else if (response.contains("ingen flere sjanser")) {
                Log.i("sendGjett", "Ingen flere forsøk igjen")
                sendGjettButton.isEnabled = false
                startSpillButton.isEnabled = true
            }
        }
    }

    private fun performRequest(typeOfRequest: HTTP, parameterList: Map<String, String>, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val response: String = try {
                when (typeOfRequest) {
                    HTTP.GET -> network.get(parameterList)
                    HTTP.POST -> network.post(parameterList)
                    HTTP.GET_WITH_HEADER -> network.getWithHeader(parameterList)
                }
            } catch (e: Exception) {
                Log.e("performRequest", "Feil ved forespørsel: ${e.message}")
                "Feil ved forespørsel"
            }

            withContext(Dispatchers.Main) {
                callback(response)
            }
        }
    }
}
