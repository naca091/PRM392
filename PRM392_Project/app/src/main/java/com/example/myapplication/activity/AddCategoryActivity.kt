package com.example.myapplication.activity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.R
import com.example.myapplication.model.Category
import com.example.myapplication.repository.CategoryRepository
import com.google.android.material.textfield.TextInputEditText

class AddCategoryActivity : BaseActivity() {
    private lateinit var editTextCategoryName: TextInputEditText
    private lateinit var categoryRepository: CategoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Add Category")

        categoryRepository = CategoryRepository(this)

        // Init views
        editTextCategoryName = findViewById(R.id.editTextCategoryName)
        val buttonSaveCategory = findViewById<Button>(R.id.buttonSaveCategory)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        // Set listeners
        buttonSaveCategory.setOnClickListener { saveCategory() }
        buttonBack.setOnClickListener { onBackPressed() }
    }

    private fun saveCategory() {
        val categoryName = editTextCategoryName.text.toString().trim()
        
        if (categoryName.isEmpty()) {
            editTextCategoryName.error = "Please enter category name"
            return
        }

        val category = Category(name = categoryName)
        val result = categoryRepository.insertCategory(category)

        if (result > 0) {
            Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show()
        }
    }
} 