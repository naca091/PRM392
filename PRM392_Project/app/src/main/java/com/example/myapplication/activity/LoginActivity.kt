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
import com.example.myapplication.util.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : BaseActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Login")
        
        userRepository = UserRepository(this)
        sessionManager = SessionManager(this)
        
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMainScreen()
            finish()
            return
        }
        
        val editTextUsername = findViewById<TextInputEditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<TextInputEditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<MaterialButton>(R.id.buttonLogin)
        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
        val textViewSignUp = findViewById<TextView>(R.id.textViewSignUp)
        
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val user = userRepository.getUserByUsername(username)
            
            if (user != null && user.password == password) {
                // Login successful
                sessionManager.createLoginSession(user)
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                navigateToMainScreen()
                finish()
            } else {
                // Login failed
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Thêm xử lý cho TextView Sign Up
        val signUpText = findViewById<TextView>(R.id.textViewSignUp)
        if (signUpText != null) {
            signUpText.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        } else {
            // Fallback cho Button cũ (nếu tồn tại)
            val buttonRegister = findViewById<Button>(R.id.buttonRegister)
            buttonRegister?.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
        
        // Xử lý sự kiện Forgot Password
        textViewForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot password feature not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun navigateToMainScreen() {
        val intent = if (sessionManager.isAdmin()) {
            Intent(this, AdminActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
    }
} 