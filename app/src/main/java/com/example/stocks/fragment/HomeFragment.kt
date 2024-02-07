package com.example.stocks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.stocks.MainActivity
import com.example.stocks.R
import com.example.stocks.adapters.LoadingSpinner
import com.example.stocks.adapters.PostAdapter
import com.example.stocks.cache.AppDatabase
import com.example.stocks.cache.HomeViewModelFactory
import com.example.stocks.viewModel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var loadingSpinner: LoadingSpinner
    private lateinit var toggleMyPosts: ToggleButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "app_database"
        ).build()

        val postDao = database.postDao()
        val homeViewModelFactory = HomeViewModelFactory(postDao)
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]

        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        loadingSpinner = view.findViewById(R.id.loadingSpinner)
        postAdapter = PostAdapter(requireContext(), findNavController(), listOf())
        postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        postRecyclerView.adapter = postAdapter

        homeViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.posts = posts
            postAdapter.notifyDataSetChanged()
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                loadingSpinner.showLoading()
            } else {
                loadingSpinner.hideLoading()
            }
        }

        toggleMyPosts = view.findViewById(R.id.toggleMyPosts)
        toggleMyPosts.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                homeViewModel.filterMyPosts()
            } else {
                homeViewModel.fetchPosts()
            }
        }

        homeViewModel.fetchPosts()
        return view
    }
}
