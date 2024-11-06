package com.example.oving4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView

class DetailFragment : Fragment() {

    private var movieIndex: Int = 0
    private lateinit var movieTitles: Array<String>
    private lateinit var movieDescriptions: Array<String>
    private lateinit var movieImages: IntArray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_detail, container, false)

        movieTitles = resources.getStringArray(R.array.movie_titles)
        movieDescriptions = resources.getStringArray(R.array.movie_descriptions)
        movieImages = intArrayOf(R.drawable.interstellar, R.drawable.cars, R.drawable.toy_story, R.drawable.joker, R.drawable.kingsman)

        if (arguments != null) {
            movieIndex = arguments?.getInt("movieIndex", 0) ?: 0
        }

        updateMovieDetails(rootView)
        return rootView
    }

    private fun updateMovieDetails(view: View) {
        val titleTextView = view.findViewById<TextView>(R.id.movie_title)
        val descriptionTextView = view.findViewById<TextView>(R.id.movie_description)
        val imageView = view.findViewById<ImageView>(R.id.movie_image)

        titleTextView.text = movieTitles[movieIndex]
        descriptionTextView.text = movieDescriptions[movieIndex]
        imageView.setImageResource(movieImages[movieIndex])
    }

    companion object {
        fun newInstance(index: Int): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putInt("movieIndex", index)
            fragment.arguments = args
            return fragment
        }
    }
}
