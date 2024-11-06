package com.example.oving2oppgave2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RandomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val upperLimit = intent.getIntExtra("UPPER_LIMIT", 100)
        val randomValue1 = (0..upperLimit).random()
        val randomValue2 = (0..upperLimit).random()

        //We add randomValue1 and randomValue2 so we can use one method for obtaining 2 random numbers

        // Toast.makeText(this, "Random number: $randomValue", Toast.LENGTH_LONG).show()

        val resultIntent = Intent()
        resultIntent.putExtra("RANDOM_RESULT_1", randomValue1)
        resultIntent.putExtra("RANDOM_RESULT_2", randomValue2)
        setResult(Activity.RESULT_OK, resultIntent)

        finish()
    }
}