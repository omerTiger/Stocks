package com.example.stocks

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stocks.viewModel.LoginViewModel

class LoginActivity : AppCompatActivity() {


        private fun loginUser() {
                val emailEditText = findViewById<EditText>(R.id.loginEmailEditText)
                val passwordEditText = findViewById<EditText>(R.id.loginPasswordEditText)

                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                viewModel.loginUser(email, password)
        }
}
