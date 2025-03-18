package com.example.myapplication.repository

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.database.BookstoreDbHelper
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_CATEGORY_NAME
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ID
import com.example.myapplication.database.BookstoreDbHelper.Companion.TABLE_CATEGORIES
import com.example.myapplication.model.Category

class CategoryRepository(context: Context) {
    private val dbHelper = BookstoreDbHelper(context)
    
    fun getAllCategories(): List<Category> {
        val categories = mutableListOf<Category>()
        val db = dbHelper.readableDatabase
        
        val cursor = db.query(
            TABLE_CATEGORIES,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_CATEGORY_NAME ASC"
        )
        
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_CATEGORY_NAME))
                
                categories.add(Category(id, name))
            }
        }
        
        cursor.close()
        return categories
    }
    
    fun getCategoryById(categoryId: Int): Category? {
        val db = dbHelper.readableDatabase
        
        val cursor = db.query(
            TABLE_CATEGORIES,
            null,
            "$COLUMN_ID = ?",
            arrayOf(categoryId.toString()),
            null,
            null,
            null
        )
        
        var category: Category? = null
        
        with(cursor) {
            if (moveToFirst()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_CATEGORY_NAME))
                
                category = Category(id, name)
            }
        }
        
        cursor.close()
        return category
    }
    
    fun insertCategory(category: Category): Long {
        val db = dbHelper.writableDatabase
        
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, category.name)
        }
        
        return db.insert(TABLE_CATEGORIES, null, values)
    }
    
    fun updateCategory(category: Category): Int {
        val db = dbHelper.writableDatabase
        
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, category.name)
        }
        
        return db.update(
            TABLE_CATEGORIES,
            values,
            "$COLUMN_ID = ?",
            arrayOf(category.id.toString())
        )
    }
    
    fun deleteCategory(categoryId: Int): Int {
        val db = dbHelper.writableDatabase
        
        return db.delete(
            TABLE_CATEGORIES,
            "$COLUMN_ID = ?",
            arrayOf(categoryId.toString())
        )
    }
} 