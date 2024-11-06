package com.example.oving7

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "MovieDatabase"
        const val DATABASE_VERSION = 1

        const val ID = "_id"

        const val TABLE_MOVIE = "MOVIE"
        const val MOVIE_TITLE = "title"

        const val TABLE_DIRECTOR = "DIRECTOR"
        const val DIRECTOR_NAME = "name"

        const val TABLE_ACTOR = "ACTOR"
        const val ACTOR_NAME = "name"

        const val TABLE_MOVIE_DIRECTOR = "MOVIE_DIRECTOR"
        const val DIRECTOR_ID = "director_id"
        const val MOVIE_ID = "movie_id"

        const val TABLE_MOVIE_ACTOR = "MOVIE_ACTOR"
        const val ACTOR_ID = "actor_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE $TABLE_MOVIE (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $MOVIE_TITLE TEXT NOT NULL
        );""")

        db.execSQL("""CREATE TABLE $TABLE_DIRECTOR (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $DIRECTOR_NAME TEXT UNIQUE NOT NULL
        );""")

        db.execSQL("""CREATE TABLE $TABLE_ACTOR (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $ACTOR_NAME TEXT UNIQUE NOT NULL
        );""")

        db.execSQL("""CREATE TABLE $TABLE_MOVIE_DIRECTOR (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $MOVIE_ID INTEGER,
            $DIRECTOR_ID INTEGER,
            FOREIGN KEY($MOVIE_ID) REFERENCES $TABLE_MOVIE($ID),
            FOREIGN KEY($DIRECTOR_ID) REFERENCES $TABLE_DIRECTOR($ID)
        );""")

        db.execSQL("""CREATE TABLE $TABLE_MOVIE_ACTOR (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $MOVIE_ID INTEGER,
            $ACTOR_ID INTEGER,
            FOREIGN KEY($MOVIE_ID) REFERENCES $TABLE_MOVIE($ID),
            FOREIGN KEY($ACTOR_ID) REFERENCES $TABLE_ACTOR($ID)
        );""")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIE_ACTOR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIE_DIRECTOR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACTOR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DIRECTOR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIE")
        onCreate(db)
    }

    fun clear() {
        writableDatabase.use { onUpgrade(it, 0, 0) }
    }

    private fun insertValue(database: SQLiteDatabase, table: String, field: String, value: String): Long {
        val values = ContentValues()
        values.put(field, value.trim())
        return database.insert(table, null, values)
    }

    private fun insertValueIfNotExists(database: SQLiteDatabase, table: String, field: String, value: String): Long {
        database.query(table, arrayOf(ID), "$field=?", arrayOf(value), null, null, null).use { cursor ->
            return if (cursor.moveToFirst()) {
                cursor.getLong(cursor.getColumnIndexOrThrow(ID))
            } else {
                insertValue(database, table, field, value)
            }
        }
    }

    private fun linkMovieAndDirector(database: SQLiteDatabase, movieId: Long, directorId: Long) {
        val values = ContentValues().apply {
            put(MOVIE_ID, movieId)
            put(DIRECTOR_ID, directorId)
        }
        database.insert(TABLE_MOVIE_DIRECTOR, null, values)
    }

    private fun linkMovieAndActor(database: SQLiteDatabase, movieId: Long, actorId: Long) {
        val values = ContentValues().apply {
            put(MOVIE_ID, movieId)
            put(ACTOR_ID, actorId)
        }
        database.insert(TABLE_MOVIE_ACTOR, null, values)
    }

    fun insertMovie(title: String, director: String, actors: List<String>) {
        writableDatabase.use { database ->
            val movieId = insertValueIfNotExists(database, TABLE_MOVIE, MOVIE_TITLE, title)
            val directorId = insertValueIfNotExists(database, TABLE_DIRECTOR, DIRECTOR_NAME, director)
            linkMovieAndDirector(database, movieId, directorId)

            actors.forEach { actor ->
                val actorId = insertValueIfNotExists(database, TABLE_ACTOR, ACTOR_NAME, actor)
                linkMovieAndActor(database, movieId, actorId)
            }
        }
    }

    fun getAllMoviesWithDetails(): List<String> {
        val moviesData = mutableListOf<String>()
        val query = """
        SELECT M.${MOVIE_TITLE} AS title, 
               D.${DIRECTOR_NAME} AS director,
               GROUP_CONCAT(A.${ACTOR_NAME}, ', ') AS actors
        FROM $TABLE_MOVIE M
        LEFT JOIN $TABLE_MOVIE_DIRECTOR MD ON M.$ID = MD.$MOVIE_ID
        LEFT JOIN $TABLE_DIRECTOR D ON MD.$DIRECTOR_ID = D.$ID
        LEFT JOIN $TABLE_MOVIE_ACTOR MA ON M.$ID = MA.$MOVIE_ID
        LEFT JOIN $TABLE_ACTOR A ON MA.$ACTOR_ID = A.$ID
        GROUP BY M.${MOVIE_TITLE}
    """
        readableDatabase.rawQuery(query, null).use { cursor ->
            while (cursor.moveToNext()) {
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val director = cursor.getString(cursor.getColumnIndexOrThrow("director"))
                val actors = cursor.getString(cursor.getColumnIndexOrThrow("actors"))
                moviesData.add("Title: $title, Director: $director, Actors: $actors")
            }
        }
        return moviesData
    }

    fun getMoviesByDirector(directorName: String): List<String> {
        val movies = mutableListOf<String>()
        val query = """
        SELECT M.${MOVIE_TITLE} AS title
        FROM $TABLE_MOVIE M
        JOIN $TABLE_MOVIE_DIRECTOR MD ON M.$ID = MD.$MOVIE_ID
        JOIN $TABLE_DIRECTOR D ON MD.$DIRECTOR_ID = D.$ID
        WHERE D.$DIRECTOR_NAME = ?
    """
        readableDatabase.rawQuery(query, arrayOf(directorName)).use { cursor ->
            while (cursor.moveToNext()) {
                movies.add(cursor.getString(cursor.getColumnIndexOrThrow("title")))
            }
        }
        return movies
    }

    fun getActorsByMovie(title: String): List<String> {
        val actors = mutableListOf<String>()
        val query = """
        SELECT A.${ACTOR_NAME} AS actor
        FROM $TABLE_ACTOR A
        JOIN $TABLE_MOVIE_ACTOR MA ON A.$ID = MA.$ACTOR_ID
        JOIN $TABLE_MOVIE M ON MA.$MOVIE_ID = M.$ID
        WHERE M.$MOVIE_TITLE = ?
    """
        readableDatabase.rawQuery(query, arrayOf(title)).use { cursor ->
            while (cursor.moveToNext()) {
                actors.add(cursor.getString(cursor.getColumnIndexOrThrow("actor")))
            }
        }
        return actors
    }

    fun getAllDirectors(): List<String> {
        val directors = mutableListOf<String>()
        val query = "SELECT DISTINCT $DIRECTOR_NAME FROM $TABLE_DIRECTOR"
        readableDatabase.rawQuery(query, null).use { cursor ->
            while (cursor.moveToNext()) {
                directors.add(cursor.getString(cursor.getColumnIndexOrThrow(DIRECTOR_NAME)))
            }
        }
        return directors
    }

    fun getAllMovies(): List<String> {
        val movies = mutableListOf<String>()
        val query = "SELECT DISTINCT $MOVIE_TITLE FROM $TABLE_MOVIE"
        readableDatabase.rawQuery(query, null).use { cursor ->
            while (cursor.moveToNext()) {
                movies.add(cursor.getString(cursor.getColumnIndexOrThrow(MOVIE_TITLE)))
            }
        }
        return movies
    }

}
