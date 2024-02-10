package com.example.stocks.viewModel
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stocks.domin.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class SignupViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

    private val _signupSuccess = MutableLiveData<Boolean>()
    val signupSuccess: LiveData<Boolean> get() = _signupSuccess

    fun signupUser(userName: String, fullName: String, email: String, password: String, imageUri: Uri?) {
        auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveUserInfo(userName, fullName, email, imageUri!!)
            } else {
                _signupSuccess.value = false
            }
        }
    }

    private fun saveUserInfo(userName: String, fullName: String, email: String, imageUri: Uri) {
    val currentUserId: String = FirebaseAuth.getInstance().currentUser!!.uid

    val imageName = UUID.randomUUID().toString()
    val imageRef = storageReference.child("post images/$imageName.jpg")

    imageRef.putFile(imageUri)
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                userRef.child(currentUserId).setValue(
                    User(currentUserId, userName.lowercase(), fullName, email, uri.toString(), "")
                )
                _signupSuccess.value = true
            }
        }
        .addOnFailureListener {
            _signupSuccess.value = false
        }
    }
}