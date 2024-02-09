package com.example.stocks.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.stocks.R
import com.example.stocks.adapters.LoadingSpinner
import com.example.stocks.viewModel.AddPostViewModel

class AddPostFragment : Fragment() {

    private lateinit var addPostViewModel: AddPostViewModel
    private lateinit var pictureImageView: ImageView
    private lateinit var captionEditText: EditText
    private lateinit var loadingSpinner: LoadingSpinner
    private var editPostImageUrl: Uri? = null
    private var isUpdate: Boolean = false
    private var selectedImageUri: Uri? = null


    private val args: AddPostFragmentArgs by navArgs()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                selectedImageUri = data?.data
                pictureImageView.setImageURI(data?.data)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)
        captionEditText = view.findViewById(R.id.captionTextView)
        loadingSpinner = view.findViewById(R.id.loadingSpinner)
        addPostViewModel = ViewModelProvider(requireActivity())[AddPostViewModel::class.java]

        pictureImageView = view.findViewById(R.id.pictureImageView)
        pictureImageView.setOnClickListener {
            openGallery()
        }

        val dontPostPicture: ImageButton = view.findViewById(R.id.dontPostButton)
        dontPostPicture.setOnClickListener {
            findNavController().navigate(R.id.action_addPostFragment_to_homeFragment)
            //activity?.finish()
        }

        val postButton: ImageView = view.findViewById(R.id.post_picture)
        postButton.setOnClickListener {
            val caption = captionEditText.text.toString()
            if (isUpdate) {
                addPostViewModel.updatePost(editPostImageUrl, selectedImageUri, caption, args.postId)
            } else {
                addPostViewModel.uploadPost(selectedImageUri, caption)
            }

            observeUploadStatus()
        }

        if (args.postId.isNotBlank()){
            editPostImageUrl = Uri.parse(args.imageUrl)
            val editPostCaption = args.caption

            setImageUriLogic(args.imageUrl, view)
            setCaptionLogic(editPostCaption, view)
            isUpdate = true
        }

        return view
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        getContent.launch(intent)
    }

    private fun observeUploadStatus() {
        addPostViewModel.uploadStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Post uploaded successfully", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_addPostFragment_to_homeFragment)
            } else {
                Toast.makeText(requireContext(), "Failed to upload post", Toast.LENGTH_SHORT).show()
            }
        }

        addPostViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                loadingSpinner.showLoading()
            } else {
                loadingSpinner.hideLoading()
            }
        }
    }

    private fun setCaptionLogic(caption: String, view: View) {
        val captionEditText: EditText = view.findViewById(R.id.captionTextView)
        captionEditText.setText(caption)
    }

    private fun setImageUriLogic(imageUrl: String, view: View) {
        val pictureImageView: ImageView = view.findViewById(R.id.pictureImageView)
        Glide.with(requireContext())
            .load(imageUrl)
            .into(pictureImageView)

    }
}
