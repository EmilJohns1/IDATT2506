package com.example.oving4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity(), ListFragment.OnMovieSelectedListener {

    private var currentIndex: Int = 0
    private lateinit var movieTitles: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieTitles = resources.getStringArray(R.array.movie_titles)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.list_fragment_container, ListFragment())
                .replace(R.id.detail_fragment_container, DetailFragment.newInstance(currentIndex))
                .commit()
        }
    }

    override fun onMovieSelected(index: Int) {
        currentIndex = index
        val detailFragment = DetailFragment.newInstance(index)
        supportFragmentManager.beginTransaction()
            .replace(R.id.detail_fragment_container, detailFragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_next -> {
                navigateToNextMovie()
                true
            }
            R.id.menu_previous -> {
                navigateToPreviousMovie()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToNextMovie() {
        if (currentIndex < movieTitles.size - 1) {
            currentIndex++
            onMovieSelected(currentIndex)
        }
    }

    private fun navigateToPreviousMovie() {
        if (currentIndex > 0) {
            currentIndex--
            onMovieSelected(currentIndex)
        }
    }
}