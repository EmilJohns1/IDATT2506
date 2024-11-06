package com.example.oving2oppgave1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val upperLimit = intent.getIntExtra("UPPER_LIMIT", 100)
        val randomValue = (0..upperLimit).random()

        Toast.makeText(this, "Random number: $randomValue", Toast.LENGTH_LONG).show()

        val resultIntent = Intent()
        resultIntent.putExtra("RANDOM_RESULT", randomValue)
        setResult(Activity.RESULT_OK, resultIntent)

        finish()
    }
}