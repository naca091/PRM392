package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.R
import com.example.myapplication.model.User
import com.example.myapplication.repository.UserRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : BaseActivity() {
    private lateinit var userRepository: UserRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Sign Up")
        
        userRepository = UserRepository(this)
        
        val editTextEmail = findViewById<TextInputEditText>(R.id.editTextRegisterEmail)
        val editTextUsername = findViewById<TextInputEditText>(R.id.editTextRegisterUsername)
        val editTextPassword = findViewById<TextInputEditText>(R.id.editTextRegisterPassword)
        val buttonRegister = findViewById<MaterialButton>(R.id.buttonRegisterUser)
        
        val textViewLogin = findViewById<TextView>(R.id.textViewLogin)
        if (textViewLogin == null) {
            val buttonBackToLogin = findViewById<Button>(R.id.buttonBackToLogin)
            buttonBackToLogin?.setOnClickListener {
                finish() // Go back to login screen
            }
        } else {
            textViewLogin.setOnClickListener {
                finish() // Go back to login screen
            }
        }
        
        buttonRegister.setOnClickListener {
            val email = editTextEmail?.text?.toString()?.trim() ?: ""
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            
            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Check if username already exists
            val existingUser = userRepository.getUserByUsername(username)
            if (existingUser != null) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Create new user
            val newUser = User(
                username = username,
                password = password,
                isAdmin = false // Regular user by default
            )
            
            val userId = userRepository.insertUser(newUser)
            
            if (userId > 0) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                finish() // Go back to login screen
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 