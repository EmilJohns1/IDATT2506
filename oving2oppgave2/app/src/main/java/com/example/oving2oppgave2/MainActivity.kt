package com.example.oving2oppgave2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var number1TextView: TextView
    private lateinit var number2TextView: TextView
    private lateinit var answerEditText: EditText
    private lateinit var upperLimitEditText: EditText

    private var number1: Int = 0
    private var number2: Int = 0

    private val getRandomNumbersResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                number1 = result.data?.getIntExtra("RANDOM_RESULT_1", -1) ?: -1
                number2 = result.data?.getIntExtra("RANDOM_RESULT_2", -1) ?: -1
                updateNumberTextViews()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        number1TextView = findViewById(R.id.number1TextView)
        number2TextView = findViewById(R.id.number2TextView)
        answerEditText = findViewById(R.id.answerEditText)
        upperLimitEditText = findViewById(R.id.upperLimitEditText)
        val addButton = findViewById<Button>(R.id.addButton)
        val multiplyButton = findViewById<Button>(R.id.multiplyButton)

        upperLimitEditText.setText("10")
        startNewRandomNumbers()

        addButton.setOnClickListener {
            checkAnswer("add")
            startNewRandomNumbers()
        }

        multiplyButton.setOnClickListener {
            checkAnswer("multiply")
            startNewRandomNumbers()
        }
    }

    private fun updateNumberTextViews() {
        number1TextView.text = number1.toString()
        number2TextView.text = number2.toString()
    }

    private fun startNewRandomNumbers() {
        val intent = Intent(this, RandomActivity::class.java)
        intent.putExtra("UPPER_LIMIT", upperLimitEditText.text.toString().toIntOrNull() ?: 10)
        getRandomNumbersResult.launch(intent)
    }

    private fun checkAnswer(operation: String) {
        val userInput = answerEditText.text.toString().toIntOrNull()
        if (userInput != null) {
            val correctAnswer = when (operation) {
                "add" -> number1 + number2
                "multiply" -> number1 * number2
                else -> 0
            }

            if (userInput == correctAnswer) {
                Toast.makeText(this, getString(R.string.riktig), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.feil) + correctAnswer, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.feil_string), Toast.LENGTH_SHORT).show()
        }
    }
}
