package com.example.stocks.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stocks.R
import com.example.stocks.domin.Post
import com.example.stocks.domin.User
import com.example.stocks.fragment.HomeFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PostAdapter(private val context: Context, private val navController: NavController, var posts: List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        val captionTextView: TextView = itemView.findViewById(R.id.captionTextView)
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)

        init {
            itemView.setOnLongClickListener {
                showPopupMenu(it)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        publisherInfo(holder.usernameTextView,post.userId!!)
        loadImage(post, holder)

        holder.captionTextView.text = post.caption
        holder.itemView.tag = posts[position].id

    }

    private fun loadImage(post: Post, holder: PostViewHolder) {
        Glide.with(context)
            .load(post.imageUrl)
            .into(holder.postImageView)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    private fun publisherInfo(username: TextView, publisherID: String) {

        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userRef.addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user = snapshot.getValue(User::class.java)
                    username.text =(user!!.username)
                }
            }

        })
    }

    private fun showPopupMenu(view: View) {
        val postId = view.tag as String
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid


        getPostById(postId) { post ->
            if (post?.userId == currentUserID) {
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.post_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_delete_post -> {
                            deletePost(postId)
                        }

                        R.id.action_edit_post -> {
                            editPost(post!!)
                        }

                        else -> false
                    }
                }

                popupMenu.show()
            }
        }
    }

    private fun getPostById(postId: String, callback: (Post?) -> Unit) {
        val postRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("posts").child(postId)

        postRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post: Post? = snapshot.getValue(Post::class.java)
                callback(post)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    private fun editPost(post: Post): Boolean {
        val action = HomeFragmentDirections.actionEditPost(
            postId = post.id,
            imageUrl = post.imageUrl,
            caption = post.caption,
            isForUpdate = true
        )
        navController.navigate(action)

        return true
    }

    private fun deletePost(postId: String): Boolean {
        val post = FirebaseDatabase.getInstance().getReference("posts/$postId")
        post.get().addOnSuccessListener {
            val imageUrl = it.child("imageUrl").value.toString()
            post.removeValue()
            FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl).delete()
        }

        return true
    }
}