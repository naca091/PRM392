package com.example.myapplication.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.User
import com.example.myapplication.repository.UserRepository
import com.example.myapplication.util.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UserManagementActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoUsers: TextView
    private lateinit var emptyStateContainer: View
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "User Management")
        
        userRepository = UserRepository(this)
        sessionManager = SessionManager(this)
        
        // Ensure user is admin
        if (!sessionManager.isAdmin()) {
            finish()
            return
        }
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewUsers)
        textViewNoUsers = findViewById(R.id.textViewNoUsers)
        emptyStateContainer = findViewById(R.id.emptyStateContainer)
        val fabAddUser = findViewById<FloatingActionButton>(R.id.fabAddUser)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Setup FAB for adding users
        fabAddUser.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }
        
        // Load users
        loadUsers()
    }
    
    override fun onResume() {
        super.onResume()
        // Reload users when returning to this activity
        loadUsers()
    }
    
    private fun loadUsers() {
        val users = userRepository.getAllUsers()
        
        if (users.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateContainer.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateContainer.visibility = View.GONE
            
            recyclerView.adapter = UserAdapter(users)
        }
    }
    
    private fun showAddUserDialog() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 30, 50, 30)
        
        val usernameEdit = EditText(this)
        usernameEdit.hint = "Username"
        layout.addView(usernameEdit)
        
        val passwordEdit = EditText(this)
        passwordEdit.hint = "Password"
        layout.addView(passwordEdit)
        
        val isAdminCheckbox = CheckBox(this)
        isAdminCheckbox.text = "Is Admin"
        layout.addView(isAdminCheckbox)
        
        AlertDialog.Builder(this)
            .setTitle("Add User")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val username = usernameEdit.text.toString().trim()
                val password = passwordEdit.text.toString().trim()
                val isAdmin = isAdminCheckbox.isChecked
                
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                // Check if username already exists
                val existingUser = userRepository.getUserByUsername(username)
                if (existingUser != null) {
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                val newUser = User(
                    username = username,
                    password = password,
                    isAdmin = isAdmin
                )
                
                val userId = userRepository.insertUser(newUser)
                
                if (userId > 0) {
                    Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show()
                    loadUsers() // Refresh user list
                } else {
                    Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private inner class UserAdapter(private val users: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
        
        inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val usernameTextView: TextView = itemView.findViewById(R.id.textViewUsername)
            val roleTextView: TextView = itemView.findViewById(R.id.textViewRole)
            val editButton: View = itemView.findViewById(R.id.buttonEditUser)
            val deleteButton: View = itemView.findViewById(R.id.buttonDeleteUser)
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = layoutInflater.inflate(R.layout.item_user, parent, false)
            return UserViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = users[position]
            
            holder.usernameTextView.text = user.username
            holder.roleTextView.text = if (user.isAdmin) "Admin" else "User"
            
            // Disable editing/deleting admin user (id=1)
            val isCurrentUser = user.id == sessionManager.getUserId()
            
            holder.editButton.setOnClickListener {
                val intent = Intent(this@UserManagementActivity, EditUserActivity::class.java)
                intent.putExtra("USER_ID", user.id.toInt())
                startActivity(intent)
            }
            
            holder.editButton.isEnabled = !isCurrentUser
            holder.editButton.alpha = if (isCurrentUser) 0.5f else 1.0f
            
            holder.deleteButton.setOnClickListener {
                showDeleteUserDialog(user)
            }
            
            holder.deleteButton.isEnabled = !isCurrentUser
            holder.deleteButton.alpha = if (isCurrentUser) 0.5f else 1.0f
        }
        
        override fun getItemCount() = users.size
    }
    
    private fun showEditUserDialog(user: User) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 30, 50, 30)
        
        val usernameEdit = EditText(this)
        usernameEdit.setText(user.username)
        layout.addView(usernameEdit)
        
        val passwordEdit = EditText(this)
        passwordEdit.hint = "Enter new password (leave empty to keep current)"
        layout.addView(passwordEdit)
        
        val isAdminCheckbox = CheckBox(this)
        isAdminCheckbox.text = "Is Admin"
        isAdminCheckbox.isChecked = user.isAdmin
        layout.addView(isAdminCheckbox)
        
        AlertDialog.Builder(this)
            .setTitle("Edit User")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                val username = usernameEdit.text.toString().trim()
                val password = passwordEdit.text.toString().trim()
                val isAdmin = isAdminCheckbox.isChecked
                
                if (username.isEmpty()) {
                    Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                // Check if username already exists and it's not the same user
                val existingUser = userRepository.getUserByUsername(username)
                if (existingUser != null && existingUser.id != user.id) {
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                val updatedUser = user.copy(
                    username = username,
                    password = if (password.isEmpty()) user.password else password,
                    isAdmin = isAdmin
                )
                
                val result = userRepository.updateUser(updatedUser)
                
                if (result > 0) {
                    Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show()
                    loadUsers() // Refresh user list
                } else {
                    Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showDeleteUserDialog(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Xóa người dùng")
            .setMessage("Bạn có chắc chắn muốn xóa người dùng ${user.username}?")
            .setPositiveButton("Xóa") { _, _ ->
                val result = userRepository.deleteUser(user.id)
                
                if (result > 0) {
                    Toast.makeText(this, "Xóa người dùng thành công", Toast.LENGTH_SHORT).show()
                    loadUsers() // Refresh user list
                } else {
                    Toast.makeText(this, "Xóa người dùng thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
} 