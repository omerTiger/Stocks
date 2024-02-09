package com.example.stocks.fragment
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.stocks.LoginActivity
import com.example.stocks.viewModel.EditProfileViewModel
import com.example.stocks.R
import com.google.firebase.auth.FirebaseAuth

class EditProfileFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var editTextFullName: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextBio: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonUpdateProfile: Button
    private lateinit var editUserImageView: ImageView
    private var selectedImageUri: Uri? = null

    private val viewModel: EditProfileViewModel by activityViewModels()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                selectedImageUri = data?.data
                editUserImageView.setImageURI(data?.data)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        getScreenElements(view)
        setImage()

        viewModel.getUserData().get().addOnSuccessListener { snapshot ->
            editTextFullName.setText(snapshot.child("fullName").value.toString())
            editTextUsername.setText(snapshot.child("username").value.toString())
            editTextBio.setText(snapshot.child("bio").value.toString())
            editTextEmail.setText(snapshot.child("email").value.toString())
            Glide.with(requireContext()).load(snapshot.child("image").value.toString()).into(editUserImageView)
        }


        buttonUpdateProfile.setOnClickListener {
            val newFullName = editTextFullName.text.toString()
            val newUsername = editTextUsername.text.toString()
            val newBio = editTextBio.text.toString()
            val email = editTextEmail.text.toString()

            viewModel.updateProfile(newFullName, newUsername, email, newBio, selectedImageUri)
            viewModel.updateSuccess.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess) {
                    findNavController().navigate(R.id.action_editProfileFragment_to_homeFragment)
                } else {
                    Toast.makeText(requireContext(), "Signup failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logoutUser()
        }

        return view
    }

    private fun getScreenElements(view: View) {
        editTextFullName = view.findViewById(R.id.editTextFullName)
        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextBio = view.findViewById(R.id.editTextBio)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        buttonUpdateProfile = view.findViewById(R.id.buttonUpdateProfile)
        editUserImageView = view.findViewById(R.id.editUserImageView)
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setImage() {
        editUserImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            getContent.launch(intent)
        }
    }
}
