package com.example.stocks.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stocks.domin.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class AddPostViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("posts")
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> get() = _uploadStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun uploadPost(imageUri: Uri?, caption: String) {
        _isLoading.value = true

        if (imageUri != null) {
            val imageName = UUID.randomUUID().toString()
            val imageRef = storageReference.child("post images/$imageName.jpg")

            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val postId = databaseReference.push().key!!
                        val post = Post(postId, imageUrl, caption, auth.currentUser?.uid)
                        databaseReference.child(postId).setValue(post)
                        _uploadStatus.postValue(true)
                    }
                }
                .addOnFailureListener { e ->
                    _uploadStatus.postValue(false)
                }
        } else {
            _uploadStatus.postValue(false)
        }

        _isLoading.value = false
    }

    fun updatePost(oldImageUri: Uri?, newImage: Uri?, caption: String, postId: String) {
        _isLoading.value = true

        val post = FirebaseDatabase.getInstance().reference.child("posts").child(postId)
        val oldStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUri.toString())
        val updatedValues = mutableMapOf<String, Any>()
        updatedValues["caption"] = caption

        if (newImage == null) {
            updateChild(updatedValues, post)
        } else {
        oldStorageReference.putFile(newImage)
            .addOnSuccessListener {
                oldStorageReference.downloadUrl.addOnSuccessListener { uri ->
                    updatedValues["imageUrl"] = uri.toString()

                    updateChild(updatedValues, post)
                }
            }
        }

        _isLoading.value = false
    }

    private fun updateChild(
        updatedValues: MutableMap<String, Any>,
        post: DatabaseReference
    ) {
        post.updateChildren(updatedValues)
            .addOnSuccessListener {
                println("Post updated successfully")
                _uploadStatus.postValue(true)
            }
            .addOnFailureListener { exception ->
                println("Failed to update post: ${exception.message}")
                _uploadStatus.postValue(false)
            }
    }
}