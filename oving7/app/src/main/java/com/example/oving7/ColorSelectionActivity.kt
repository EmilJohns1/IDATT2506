package com.example.oving7

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.oving7.databinding.ActivityColorSelectionBinding

class ColorSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityColorSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityColorSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnColorRed.setOnClickListener { saveColor(Color.RED) }
        binding.btnColorGreen.setOnClickListener { saveColor(Color.GREEN) }
        binding.btnColorBlue.setOnClickListener { saveColor(Color.BLUE) }
        binding.btnColorWhite.setOnClickListener { saveColor(Color.WHITE) }
    }

    private fun saveColor(color: Int) {
        val sharedPref = getSharedPreferences("Settings", MODE_PRIVATE)
        with (sharedPref.edit()) {
            putInt("BackgroundColor", color)
            apply()
        }
        finish()
    }
}
