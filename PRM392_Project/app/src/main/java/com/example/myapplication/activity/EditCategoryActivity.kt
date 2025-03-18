package com.example.myapplication.activity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.R
import com.example.myapplication.model.Category
import com.example.myapplication.repository.CategoryRepository
import com.google.android.material.textfield.TextInputEditText

class EditCategoryActivity : BaseActivity() {
    private lateinit var editTextCategoryName: TextInputEditText
    private lateinit var categoryRepository: CategoryRepository
    
    private var categoryId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Edit Category")

        categoryRepository = CategoryRepository(this)

        // Get category ID from intent
        categoryId = intent.getIntExtra("category_id", 0)
        if (categoryId == 0) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Init views
        editTextCategoryName = findViewById(R.id.editTextCategoryName)
        val buttonSaveCategory = findViewById<Button>(R.id.buttonSaveCategory)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        // Load category data
        loadCategoryData()

        // Set listeners
        buttonSaveCategory.setOnClickListener { updateCategory() }
        buttonBack.setOnClickListener { onBackPressed() }
    }

    private fun loadCategoryData() {
        val category = categoryRepository.getCategoryById(categoryId)
        category?.let {
            editTextCategoryName.setText(it.name)
        }
    }

    private fun updateCategory() {
        val categoryName = editTextCategoryName.text.toString().trim()
        
        if (categoryName.isEmpty()) {
            editTextCategoryName.error = "Please enter category name"
            return
        }

        val category = Category(id = categoryId, name = categoryName)
        val result = categoryRepository.updateCategory(category)

        if (result > 0) {
            Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show()
        }
    }
}