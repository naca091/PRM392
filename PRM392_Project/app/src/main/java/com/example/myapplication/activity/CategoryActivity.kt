package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.CategoryAdapter
import com.example.myapplication.model.Category
import com.example.myapplication.repository.CategoryRepository
import com.example.myapplication.util.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoryActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoCategories: TextView
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var categoryAdapter: CategoryAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Categories")
        
        categoryRepository = CategoryRepository(this)
        sessionManager = SessionManager(this)
        
        // Ensure user is admin
        if (!sessionManager.isAdmin()) {
            finish()
            return
        }
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewCategories)
        textViewNoCategories = findViewById(R.id.textViewNoCategories)
        val fabAddCategory = findViewById<FloatingActionButton>(R.id.fabAddCategory)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Setup FAB for adding categories
        fabAddCategory.setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }
        
        // Load categories
        loadCategories()
    }
    
    private fun loadCategories() {
        val categories = categoryRepository.getAllCategories()
        
        if (categories.isEmpty()) {
            recyclerView.visibility = View.GONE
            textViewNoCategories.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            textViewNoCategories.visibility = View.GONE
            
            categoryAdapter = CategoryAdapter(
                categories = categories,
                onEditClickListener = { category ->
                    val intent = Intent(this, EditCategoryActivity::class.java)
                    intent.putExtra("category_id", category.id)
                    startActivity(intent)
                },
                onDeleteClickListener = { category ->
                    confirmDeleteCategory(category)
                }
            )
            
            recyclerView.adapter = categoryAdapter
        }
    }
    
    private fun confirmDeleteCategory(category: Category) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete this category?")
            .setPositiveButton("Yes") { _, _ ->
                deleteCategory(category)
            }
            .setNegativeButton("No", null)
            .show()
    }
    
    private fun deleteCategory(category: Category) {
        val result = categoryRepository.deleteCategory(category.id)
        if (result > 0) {
            Toast.makeText(this, "Category deleted successfully", Toast.LENGTH_SHORT).show()
            loadCategories()
        } else {
            Toast.makeText(this, "Failed to delete category", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadCategories() // Reload categories when returning to this activity
    }
} 