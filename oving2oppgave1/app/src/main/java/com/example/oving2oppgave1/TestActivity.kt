package com.example.oving2oppgave1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {

    private lateinit var randomNumberTextView: TextView

    private val getRandomNumberResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {

            val randomValue = result.data?.getIntExtra("RANDOM_RESULT", -1) ?: -1
            randomNumberTextView.text = "Random Number: $randomValue"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val startActivityButton = findViewById<Button>(R.id.startActivityButton)
        randomNumberTextView = findViewById(R.id.randomNumberTextView)

        startActivityButton.setOnClickListener {
            val intent = Intent("com.example.oving2oppgave1.RANDOM_NUMBER")
            intent.putExtra("UPPER_LIMIT", 100)

            getRandomNumberResult.launch(intent)
        }
    }
}
