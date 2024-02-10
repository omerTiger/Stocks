package com.example.stocks.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.example.stocks.cache.AppDatabase
import com.example.stocks.cache.CachedPost
import com.example.stocks.cache.PostDao
import com.example.stocks.domin.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val postDao: PostDao) : ViewModel() {

    private val databaseReference = FirebaseDatabase.getInstance().reference.child("posts")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchPosts() {
        _isLoading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            val cachedPosts = postDao.getAllPosts()
            if (cachedPosts.isNotEmpty()) {
                // If there are cached posts, update LiveData with cached data
                _posts.postValue(mapCachedPostsToDomain(cachedPosts))
            }
        }

        // Then, fetch posts from Firebase
        fetchPostsFromFirebase()
    }

    fun filterMyPosts() {
        databaseReference.orderByChild("userId").equalTo(auth.currentUser?.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val myPosts = mutableListOf<Post>()
                    for (postSnapshot in snapshot.children) {
                        val post = postSnapshot.getValue(Post::class.java)
                        post?.let {
                            myPosts.add(it)
                        }
                    }
                    _posts.postValue(myPosts)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    private fun fetchPostsFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = mutableListOf<Post>()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let {
                        posts.add(it)
                    }
                }

                val cachedPosts = mapFirebasePostsToCachedPosts(posts)
                GlobalScope.launch(Dispatchers.IO) {
                    cachedPosts.forEach { cachedPost ->
                        postDao.insertPost(cachedPost)
                    }
                    withContext(Dispatchers.Main) {
                        val combinedPosts = mapCachedPostsToDomain(cachedPosts)

                        _isLoading.value = false
                        _posts.postValue(combinedPosts.reversed())
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {}


        })
    }

    private fun mapCachedPostsToDomain(cachedPosts: List<CachedPost>): List<Post> {
        return cachedPosts.map { cachedPost ->
            Post(
                cachedPost.id,
                cachedPost.imageUrl,
                cachedPost.caption,
                cachedPost.userId
            )
        }
    }

    private fun mapFirebasePostsToCachedPosts(firebasePosts: List<Post>): List<CachedPost> {
        return firebasePosts.map { post ->
            CachedPost(
                id = post.id,
                imageUrl = post.imageUrl,
                caption = post.caption,
                userId = post.userId
            )
        }
    }
}
