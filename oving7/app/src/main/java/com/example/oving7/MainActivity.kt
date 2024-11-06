package com.example.oving7

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.oving7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseManager
    private lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Database(this)
        database.saveDataToFile(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        dbHelper = DatabaseManager(this)

        loadDirectorsIntoSpinner()
        loadMoviesIntoSpinner()

        binding.btnShowMovies.setOnClickListener { displayAllMovies() }

        binding.btnMoviesByDirector.setOnClickListener {
            if (binding.spinnerMoviesByDirector.visibility == View.VISIBLE) {
                binding.spinnerMoviesByDirector.visibility = View.GONE
            } else {
                binding.spinnerMoviesByDirector.visibility = View.VISIBLE
            }
        }

        binding.spinnerMoviesByDirector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDirector = parent.getItemAtPosition(position) as String
                displayMoviesByDirector(selectedDirector)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val selectedDirector = parent.getItemAtPosition(0) as String
                displayMoviesByDirector(selectedDirector)
            }
        }

        binding.btnActorsForMovie.setOnClickListener {
            if (binding.spinnerActorsForMovie.visibility == View.VISIBLE) {
                binding.spinnerActorsForMovie.visibility = View.GONE
            } else {
                binding.spinnerActorsForMovie.visibility = View.VISIBLE
            }
        }

        binding.spinnerActorsForMovie.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMovie = parent.getItemAtPosition(position) as String
                displayActorsForMovie(selectedMovie)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val selectedMovie = parent.getItemAtPosition(0) as String
                displayActorsForMovie(selectedMovie)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPref = getSharedPreferences("Settings", MODE_PRIVATE)
        val backgroundColor = sharedPref.getInt("BackgroundColor", android.graphics.Color.WHITE)
        binding.rootLayout.setBackgroundColor(backgroundColor)
    }

    private fun loadDirectorsIntoSpinner() {
        val directors = dbHelper.getAllDirectors()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, directors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMoviesByDirector.adapter = adapter
    }

    private fun loadMoviesIntoSpinner() {
        val movies = dbHelper.getAllMovies()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, movies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerActorsForMovie.adapter = adapter
    }

    private fun displayAllMovies() {
        val movies = dbHelper.getAllMoviesWithDetails()
        binding.textViewResults.text = movies.joinToString("\n\n")
    }

    private fun displayMoviesByDirector(director: String) {
        val movies = dbHelper.getMoviesByDirector(director)
        binding.textViewResults.text = "Movies by $director:\n" + movies.joinToString("\n")
    }

    private fun displayActorsForMovie(title: String) {
        val actors = dbHelper.getActorsByMovie(title)
        binding.textViewResults.text = "Actors in $title:\n" + actors.joinToString(", ")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_color_selection -> {
                val intent = Intent(this, ColorSelectionActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
