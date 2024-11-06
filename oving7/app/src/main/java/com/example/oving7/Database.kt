package com.example.oving7

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

class Database(context: Context) : DatabaseManager(context) {

    init {
        try {
            this.clear()

            loadMoviesFromRawFile(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadMoviesFromRawFile(context: Context) {
        val inputStream = context.resources.openRawResource(R.raw.movies)
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.forEachLine { line ->
            val data = line.split(";")
            if (data.size >= 3) {
                val title = data[0].split("=")[1].trim()
                val director = data[1].split("=")[1].trim()
                val actors = data[2].split("=")[1].split(",").map { it.trim() }

                insertMovie(title, director, actors)
            }
        }
        reader.close()
    }

    fun saveDataToFile(context: Context) {
        val dbHelper = DatabaseManager(context)
        val moviesData = dbHelper.getAllMoviesWithDetails()

        val file = File(context.filesDir, "saved_movies.txt")
        FileOutputStream(file).use { fos ->
            moviesData.forEach { movieEntry ->
                fos.write("$movieEntry\n".toByteArray())
            }
        }
    }
}
