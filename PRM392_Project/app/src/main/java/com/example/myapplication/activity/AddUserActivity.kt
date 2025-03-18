package com.example.myapplication.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.R
import com.example.myapplication.model.User
import com.example.myapplication.repository.UserRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddUserActivity : BaseActivity() {
    
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var editTextUsername: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var switchIsAdmin: SwitchMaterial
    private lateinit var buttonAddUser: MaterialButton
    private lateinit var userRepository: UserRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Add New User")
        
        userRepository = UserRepository(this)
        
        // Initialize views
        usernameLayout = findViewById(R.id.usernameLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        switchIsAdmin = findViewById(R.id.switchIsAdmin)
        buttonAddUser = findViewById(R.id.buttonAddUser)
        
        // Setup button click listener
        buttonAddUser.setOnClickListener {
            addUser()
        }
    }
    
    private fun addUser() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val isAdmin = switchIsAdmin.isChecked
        
        // Validate input
        var isValid = true
        
        if (username.isEmpty()) {
            usernameLayout.error = "Username cannot be empty"
            isValid = false
        } else {
            usernameLayout.error = null
        }
        
        if (password.isEmpty()) {
            passwordLayout.error = "Password cannot be empty"
            isValid = false
        } else {
            passwordLayout.error = null
        }
        
        if (!isValid) {
            return
        }
        
        // Check if username already exists
        val existingUser = userRepository.getUserByUsername(username)
        if (existingUser != null) {
            usernameLayout.error = "Username already exists"
            return
        }
        
        // Create user
        val newUser = User(
            username = username,
            password = password,
            isAdmin = isAdmin
        )
        
        val userId = userRepository.insertUser(newUser)
        
        if (userId > 0) {
            Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show()
            finish() // Return to previous screen
        } else {
            Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
        }
    }
} 