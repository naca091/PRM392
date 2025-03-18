package com.example.myapplication.activity

import android.content.Intent
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

class EditUserActivity : BaseActivity() {
    
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var editTextUsername: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var switchIsAdmin: SwitchMaterial
    private lateinit var buttonUpdateUser: MaterialButton
    private lateinit var buttonCancel: MaterialButton
    private lateinit var userRepository: UserRepository
    
    private var userId: Int = -1
    private lateinit var currentUser: User
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Chỉnh sửa tài khoản")
        
        userRepository = UserRepository(this)
        
        // Get user ID from intent
        userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            Toast.makeText(this, "ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Get user data
        val user = userRepository.getUserById(userId)
        if (user == null) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        currentUser = user
        
        // Initialize views
        usernameLayout = findViewById(R.id.usernameLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        switchIsAdmin = findViewById(R.id.switchIsAdmin)
        buttonUpdateUser = findViewById(R.id.buttonUpdateUser)
        buttonCancel = findViewById(R.id.buttonCancel)
        
        // Fill in existing data
        editTextUsername.setText(user.username)
        switchIsAdmin.isChecked = user.isAdmin
        
        // Setup button click listeners
        buttonUpdateUser.setOnClickListener {
            updateUser()
        }
        
        buttonCancel.setOnClickListener {
            finish() // Quay lại màn hình trước đó
        }
    }
    
    private fun updateUser() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val isAdmin = switchIsAdmin.isChecked
        
        // Validate input
        var isValid = true
        
        if (username.isEmpty()) {
            usernameLayout.error = "Tên đăng nhập không được để trống"
            isValid = false
        } else {
            usernameLayout.error = null
        }
        
        if (!isValid) {
            return
        }
        
        // Check if username already exists (if changed)
        if (username != currentUser.username) {
            val existingUser = userRepository.getUserByUsername(username)
            if (existingUser != null) {
                usernameLayout.error = "Tên đăng nhập đã tồn tại"
                return
            }
        }
        
        // Update user
        val updatedUser = currentUser.copy(
            username = username,
            password = if (password.isEmpty()) currentUser.password else password,
            isAdmin = isAdmin
        )
        
        val rowsAffected = userRepository.updateUser(updatedUser)
        
        if (rowsAffected > 0) {
            Toast.makeText(this, "Cập nhật người dùng thành công", Toast.LENGTH_SHORT).show()
            finish() // Return to previous screen
        } else {
            Toast.makeText(this, "Cập nhật người dùng thất bại", Toast.LENGTH_SHORT).show()
        }
    }
} 