package com.example.stocks.cache

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stocks.viewModel.HomeViewModel

class HomeViewModelFactory(private val postDao: PostDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(postDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
