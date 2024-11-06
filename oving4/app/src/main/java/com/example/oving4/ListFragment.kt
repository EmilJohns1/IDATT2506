package com.example.oving4

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

class ListFragment : Fragment() {

    private lateinit var movieTitles: Array<String>
    private lateinit var listener: OnMovieSelectedListener

    interface OnMovieSelectedListener {
        fun onMovieSelected(index: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMovieSelectedListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement OnMovieSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_list, container, false)

        movieTitles = resources.getStringArray(R.array.movie_titles)
        val listView = rootView.findViewById<ListView>(R.id.movie_list_view)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, movieTitles)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            listener.onMovieSelected(position)
        }

        return rootView
    }
}
