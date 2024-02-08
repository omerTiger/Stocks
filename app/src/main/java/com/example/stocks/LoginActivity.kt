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

        private lateinit var viewModel: LoginViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_login)

                viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

                viewModel.loginSuccess.observe(this) { isSuccess ->
                        if (isSuccess) {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                        } else {
                                Toast.makeText(
                                        baseContext,
                                        "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                ).show()
                        }
                }

                val loginButton = findViewById<Button>(R.id.loginButton)
                loginButton.setOnClickListener {
                        loginUser()
                }

                val signupTextView: TextView = findViewById(R.id.signUpTextView)
                signupTextView.setOnClickListener {
                        startActivity(Intent(this, SignupActivity::class.java))
                }
        }

        private fun loginUser() {
                val emailEditText = findViewById<EditText>(R.id.loginEmailEditText)
                val passwordEditText = findViewById<EditText>(R.id.loginPasswordEditText)

                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                viewModel.loginUser(email, password)
        }
}
