package com.example.myapplication.repository

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.database.BookstoreDbHelper
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_BOOK_AUTHOR
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_BOOK_CATEGORY_ID
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_BOOK_IMAGE1
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_BOOK_IMAGE2
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_BOOK_IMAGE3
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_BOOK_PRICE
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_BOOK_STOCK
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_BOOK_TITLE
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_CATEGORY_NAME
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ID
import com.example.myapplication.database.BookstoreDbHelper.Companion.TABLE_BOOKS
import com.example.myapplication.database.BookstoreDbHelper.Companion.TABLE_CATEGORIES
import com.example.myapplication.model.Book

class BookRepository(context: Context) {
    private val dbHelper = BookstoreDbHelper(context)
    
    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val db = dbHelper.readableDatabase
        
        val query = """
            SELECT b.*, c.$COLUMN_CATEGORY_NAME 
            FROM $TABLE_BOOKS b
            JOIN $TABLE_CATEGORIES c ON b.$COLUMN_BOOK_CATEGORY_ID = c.$COLUMN_ID
            ORDER BY b.$COLUMN_BOOK_TITLE ASC
        """.trimIndent()
        
        val cursor = db.rawQuery(query, null)
        
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(COLUMN_BOOK_TITLE))
                val author = getString(getColumnIndexOrThrow(COLUMN_BOOK_AUTHOR))
                val price = getDouble(getColumnIndexOrThrow(COLUMN_BOOK_PRICE))
                val categoryId = getInt(getColumnIndexOrThrow(COLUMN_BOOK_CATEGORY_ID))
                val stock = getInt(getColumnIndexOrThrow(COLUMN_BOOK_STOCK))
                val categoryName = getString(getColumnIndexOrThrow(COLUMN_CATEGORY_NAME))
                
                // Lấy đường dẫn hình ảnh, xử lý trường hợp không tìm thấy cột
                val imagePath1 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE1)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                val imagePath2 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE2)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                val imagePath3 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE3)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                books.add(Book(id, title, author, price, categoryId, stock, categoryName, 
                              imagePath1, imagePath2, imagePath3))
            }
        }
        
        cursor.close()
        return books
    }
    
    fun getBookById(bookId: Int): Book? {
        val db = dbHelper.readableDatabase
        
        val query = """
            SELECT b.*, c.$COLUMN_CATEGORY_NAME 
            FROM $TABLE_BOOKS b
            JOIN $TABLE_CATEGORIES c ON b.$COLUMN_BOOK_CATEGORY_ID = c.$COLUMN_ID
            WHERE b.$COLUMN_ID = ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(bookId.toString()))
        
        var book: Book? = null
        
        with(cursor) {
            if (moveToFirst()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(COLUMN_BOOK_TITLE))
                val author = getString(getColumnIndexOrThrow(COLUMN_BOOK_AUTHOR))
                val price = getDouble(getColumnIndexOrThrow(COLUMN_BOOK_PRICE))
                val categoryId = getInt(getColumnIndexOrThrow(COLUMN_BOOK_CATEGORY_ID))
                val stock = getInt(getColumnIndexOrThrow(COLUMN_BOOK_STOCK))
                val categoryName = getString(getColumnIndexOrThrow(COLUMN_CATEGORY_NAME))
                
                // Lấy đường dẫn hình ảnh, xử lý trường hợp không tìm thấy cột
                val imagePath1 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE1)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                val imagePath2 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE2)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                val imagePath3 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE3)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                book = Book(id, title, author, price, categoryId, stock, categoryName,
                          imagePath1, imagePath2, imagePath3)
            }
        }
        
        cursor.close()
        return book
    }
    
    fun searchBooks(query: String): List<Book> {
        val books = mutableListOf<Book>()
        val db = dbHelper.readableDatabase
        
        val searchQuery = """
            SELECT b.*, c.$COLUMN_CATEGORY_NAME 
            FROM $TABLE_BOOKS b
            JOIN $TABLE_CATEGORIES c ON b.$COLUMN_BOOK_CATEGORY_ID = c.$COLUMN_ID
            WHERE b.$COLUMN_BOOK_TITLE LIKE ? OR b.$COLUMN_BOOK_AUTHOR LIKE ?
            ORDER BY b.$COLUMN_BOOK_TITLE ASC
        """.trimIndent()
        
        val searchPattern = "%$query%"
        val cursor = db.rawQuery(searchQuery, arrayOf(searchPattern, searchPattern))
        
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(COLUMN_BOOK_TITLE))
                val author = getString(getColumnIndexOrThrow(COLUMN_BOOK_AUTHOR))
                val price = getDouble(getColumnIndexOrThrow(COLUMN_BOOK_PRICE))
                val categoryId = getInt(getColumnIndexOrThrow(COLUMN_BOOK_CATEGORY_ID))
                val stock = getInt(getColumnIndexOrThrow(COLUMN_BOOK_STOCK))
                val categoryName = getString(getColumnIndexOrThrow(COLUMN_CATEGORY_NAME))
                
                // Lấy đường dẫn hình ảnh, xử lý trường hợp không tìm thấy cột
                val imagePath1 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE1)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                val imagePath2 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE2)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                val imagePath3 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE3)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                books.add(Book(id, title, author, price, categoryId, stock, categoryName, 
                              imagePath1, imagePath2, imagePath3))
            }
        }
        
        cursor.close()
        return books
    }
    
    fun getBooksByCategory(categoryId: Int): List<Book> {
        val books = mutableListOf<Book>()
        val db = dbHelper.readableDatabase
        
        val query = """
            SELECT b.*, c.$COLUMN_CATEGORY_NAME 
            FROM $TABLE_BOOKS b
            JOIN $TABLE_CATEGORIES c ON b.$COLUMN_BOOK_CATEGORY_ID = c.$COLUMN_ID
            WHERE b.$COLUMN_BOOK_CATEGORY_ID = ?
            ORDER BY b.$COLUMN_BOOK_TITLE ASC
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(categoryId.toString()))
        
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(COLUMN_BOOK_TITLE))
                val author = getString(getColumnIndexOrThrow(COLUMN_BOOK_AUTHOR))
                val price = getDouble(getColumnIndexOrThrow(COLUMN_BOOK_PRICE))
                val catId = getInt(getColumnIndexOrThrow(COLUMN_BOOK_CATEGORY_ID))
                val stock = getInt(getColumnIndexOrThrow(COLUMN_BOOK_STOCK))
                val categoryName = getString(getColumnIndexOrThrow(COLUMN_CATEGORY_NAME))
                
                // Lấy đường dẫn hình ảnh, xử lý trường hợp không tìm thấy cột
                val imagePath1 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE1)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                val imagePath2 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE2)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                val imagePath3 = try {
                    getString(getColumnIndexOrThrow(COLUMN_BOOK_IMAGE3)) ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                books.add(Book(id, title, author, price, catId, stock, categoryName, 
                              imagePath1, imagePath2, imagePath3))
            }
        }
        
        cursor.close()
        return books
    }
    
    fun insertBook(book: Book): Long {
        val db = dbHelper.writableDatabase
        
        val values = ContentValues().apply {
            put(COLUMN_BOOK_TITLE, book.title)
            put(COLUMN_BOOK_AUTHOR, book.author)
            put(COLUMN_BOOK_PRICE, book.price)
            put(COLUMN_BOOK_CATEGORY_ID, book.categoryId)
            put(COLUMN_BOOK_STOCK, book.stock)
            put(COLUMN_BOOK_IMAGE1, book.imagePath1)
            put(COLUMN_BOOK_IMAGE2, book.imagePath2)
            put(COLUMN_BOOK_IMAGE3, book.imagePath3)
        }
        
        return db.insert(TABLE_BOOKS, null, values)
    }
    
    fun updateBook(book: Book): Int {
        val db = dbHelper.writableDatabase
        
        val values = ContentValues().apply {
            put(COLUMN_BOOK_TITLE, book.title)
            put(COLUMN_BOOK_AUTHOR, book.author)
            put(COLUMN_BOOK_PRICE, book.price)
            put(COLUMN_BOOK_CATEGORY_ID, book.categoryId)
            put(COLUMN_BOOK_STOCK, book.stock)
            put(COLUMN_BOOK_IMAGE1, book.imagePath1)
            put(COLUMN_BOOK_IMAGE2, book.imagePath2)
            put(COLUMN_BOOK_IMAGE3, book.imagePath3)
        }
        
        return db.update(
            TABLE_BOOKS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(book.id.toString())
        )
    }
    
    fun updateBookStock(bookId: Int, newStock: Int): Int {
        val db = dbHelper.writableDatabase
        
        val values = ContentValues().apply {
            put(COLUMN_BOOK_STOCK, newStock)
        }
        
        return db.update(
            TABLE_BOOKS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(bookId.toString())
        )
    }
    
    fun deleteBook(bookId: Int): Int {
        val db = dbHelper.writableDatabase
        
        return db.delete(
            TABLE_BOOKS,
            "$COLUMN_ID = ?",
            arrayOf(bookId.toString())
        )
    }
    
    // Thêm phương thức để lấy tên category từ ID
    fun getCategoryNameById(categoryId: Int): String {
        val db = dbHelper.readableDatabase
        var categoryName = "Unknown"
        
        val cursor = db.query(
            TABLE_CATEGORIES,
            arrayOf(COLUMN_CATEGORY_NAME),
            "$COLUMN_ID = ?",
            arrayOf(categoryId.toString()),
            null,
            null,
            null
        )
        
        with(cursor) {
            if (moveToFirst()) {
                categoryName = getString(getColumnIndexOrThrow(COLUMN_CATEGORY_NAME))
            }
        }
        
        cursor.close()
        return categoryName
    }
    
    // Helper method to add sample books (for testing)
    fun addSampleBooks() {
        val db = dbHelper.writableDatabase
        
        // Sample books for Fiction (category id 1)
        val fiction1 = ContentValues().apply {
            put(COLUMN_BOOK_TITLE, "The Great Gatsby")
            put(COLUMN_BOOK_AUTHOR, "F. Scott Fitzgerald")
            put(COLUMN_BOOK_PRICE, 12.99)
            put(COLUMN_BOOK_CATEGORY_ID, 1)
            put(COLUMN_BOOK_STOCK, 10)
        }
        db.insert(TABLE_BOOKS, null, fiction1)
        
        val fiction2 = ContentValues().apply {
            put(COLUMN_BOOK_TITLE, "To Kill a Mockingbird")
            put(COLUMN_BOOK_AUTHOR, "Harper Lee")
            put(COLUMN_BOOK_PRICE, 14.99)
            put(COLUMN_BOOK_CATEGORY_ID, 1)
            put(COLUMN_BOOK_STOCK, 8)
        }
        db.insert(TABLE_BOOKS, null, fiction2)
        
        // Sample books for Non-Fiction (category id 2)
        val nonFiction1 = ContentValues().apply {
            put(COLUMN_BOOK_TITLE, "Sapiens: A Brief History of Humankind")
            put(COLUMN_BOOK_AUTHOR, "Yuval Noah Harari")
            put(COLUMN_BOOK_PRICE, 19.99)
            put(COLUMN_BOOK_CATEGORY_ID, 2)
            put(COLUMN_BOOK_STOCK, 15)
        }
        db.insert(TABLE_BOOKS, null, nonFiction1)
        
        // Sample books for Science (category id 3)
        val science1 = ContentValues().apply {
            put(COLUMN_BOOK_TITLE, "A Brief History of Time")
            put(COLUMN_BOOK_AUTHOR, "Stephen Hawking")
            put(COLUMN_BOOK_PRICE, 15.99)
            put(COLUMN_BOOK_CATEGORY_ID, 3)
            put(COLUMN_BOOK_STOCK, 12)
        }
        db.insert(TABLE_BOOKS, null, science1)
        
        // Sample books for History (category id 4)
        val history1 = ContentValues().apply {
            put(COLUMN_BOOK_TITLE, "The Guns of August")
            put(COLUMN_BOOK_AUTHOR, "Barbara W. Tuchman")
            put(COLUMN_BOOK_PRICE, 16.99)
            put(COLUMN_BOOK_CATEGORY_ID, 4)
            put(COLUMN_BOOK_STOCK, 7)
        }
        db.insert(TABLE_BOOKS, null, history1)
        
        // Sample books for Technology (category id 5)
        val tech1 = ContentValues().apply {
            put(COLUMN_BOOK_TITLE, "Clean Code")
            put(COLUMN_BOOK_AUTHOR, "Robert C. Martin")
            put(COLUMN_BOOK_PRICE, 24.99)
            put(COLUMN_BOOK_CATEGORY_ID, 5)
            put(COLUMN_BOOK_STOCK, 20)
        }
        db.insert(TABLE_BOOKS, null, tech1)
    }
} 