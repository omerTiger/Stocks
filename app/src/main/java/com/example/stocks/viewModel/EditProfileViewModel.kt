package com.example.stocks.viewModel
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stocks.domin.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class EditProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    fun getUserData() = databaseReference.child(auth.currentUser!!.uid)

    private fun updateProfileWithoutImage(fullName: String, username: String, bio: String) {
        auth.currentUser?.uid?.let {
            databaseReference.child(it).child("username").setValue(username)
            databaseReference.child(it).child("fullName").setValue(fullName)
            databaseReference.child(it).child("bio").setValue(bio)
            _updateSuccess.value = true
        }
    }

    fun updateProfile(fullName: String, userName: String, email: String, bio: String, imageUri: Uri?) {

        if (imageUri == null) {
            updateProfileWithoutImage(fullName, userName, bio)
        } else {
            val currentUserId: String = FirebaseAuth.getInstance().currentUser!!.uid

            val imageName = UUID.randomUUID().toString()
            val imageRef = storageReference.child("post images/$imageName.jpg")

            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        databaseReference.child(currentUserId).setValue(
                            User(currentUserId,
                                userName.lowercase(),
                                fullName,
                                email,
                                uri.toString(),
                                bio)
                        )
                        _updateSuccess.value = true
                    }
                }
                .addOnFailureListener {
                    _updateSuccess.value = false
                }
        }
    }
}
