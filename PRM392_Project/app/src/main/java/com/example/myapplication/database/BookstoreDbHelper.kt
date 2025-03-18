package com.example.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookstoreDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bookstore.db"
        private const val DATABASE_VERSION = 2

        // Table Names
        const val TABLE_USERS = "users"
        const val TABLE_CATEGORIES = "categories"
        const val TABLE_BOOKS = "books"
        const val TABLE_ORDERS = "orders"
        const val TABLE_ORDER_ITEMS = "order_items"

        // Common Columns
        const val COLUMN_ID = "id"
        
        // Users Table Columns
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_IS_ADMIN = "is_admin"
        
        // Categories Table Columns
        const val COLUMN_CATEGORY_NAME = "name"
        
        // Books Table Columns
        const val COLUMN_BOOK_TITLE = "title"
        const val COLUMN_BOOK_AUTHOR = "author"
        const val COLUMN_BOOK_PRICE = "price"
        const val COLUMN_BOOK_CATEGORY_ID = "category_id"
        const val COLUMN_BOOK_STOCK = "stock"
        const val COLUMN_BOOK_IMAGE1 = "image_path1"
        const val COLUMN_BOOK_IMAGE2 = "image_path2"
        const val COLUMN_BOOK_IMAGE3 = "image_path3"
        
        // Orders Table Columns
        const val COLUMN_ORDER_USER_ID = "user_id"
        const val COLUMN_ORDER_DATE = "order_date"
        const val COLUMN_ORDER_STATUS = "status"
        const val COLUMN_ORDER_TOTAL = "total"
        
        // Order Items Table Columns
        const val COLUMN_ORDER_ITEM_ORDER_ID = "order_id"
        const val COLUMN_ORDER_ITEM_BOOK_ID = "book_id"
        const val COLUMN_ORDER_ITEM_QUANTITY = "quantity"
        const val COLUMN_ORDER_ITEM_PRICE = "price"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Users Table
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_IS_ADMIN INTEGER DEFAULT 0
            )
        """.trimIndent()
        
        // Create Categories Table
        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT UNIQUE NOT NULL
            )
        """.trimIndent()
        
        // Create Books Table with image columns
        val createBooksTable = """
            CREATE TABLE $TABLE_BOOKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BOOK_TITLE TEXT NOT NULL,
                $COLUMN_BOOK_AUTHOR TEXT NOT NULL,
                $COLUMN_BOOK_PRICE REAL NOT NULL,
                $COLUMN_BOOK_CATEGORY_ID INTEGER NOT NULL,
                $COLUMN_BOOK_STOCK INTEGER DEFAULT 0,
                $COLUMN_BOOK_IMAGE1 TEXT DEFAULT '',
                $COLUMN_BOOK_IMAGE2 TEXT DEFAULT '',
                $COLUMN_BOOK_IMAGE3 TEXT DEFAULT '',
                FOREIGN KEY ($COLUMN_BOOK_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_ID)
            )
        """.trimIndent()
        
        // Create Orders Table
        val createOrdersTable = """
            CREATE TABLE $TABLE_ORDERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ORDER_USER_ID INTEGER NOT NULL,
                $COLUMN_ORDER_DATE TEXT NOT NULL,
                $COLUMN_ORDER_STATUS TEXT NOT NULL,
                $COLUMN_ORDER_TOTAL REAL NOT NULL,
                FOREIGN KEY ($COLUMN_ORDER_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """.trimIndent()
        
        // Create Order Items Table
        val createOrderItemsTable = """
            CREATE TABLE $TABLE_ORDER_ITEMS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ORDER_ITEM_ORDER_ID INTEGER NOT NULL,
                $COLUMN_ORDER_ITEM_BOOK_ID INTEGER NOT NULL,
                $COLUMN_ORDER_ITEM_QUANTITY INTEGER NOT NULL,
                $COLUMN_ORDER_ITEM_PRICE REAL NOT NULL,
                FOREIGN KEY ($COLUMN_ORDER_ITEM_ORDER_ID) REFERENCES $TABLE_ORDERS($COLUMN_ID),
                FOREIGN KEY ($COLUMN_ORDER_ITEM_BOOK_ID) REFERENCES $TABLE_BOOKS($COLUMN_ID)
            )
        """.trimIndent()
        
        db.execSQL(createUsersTable)
        db.execSQL(createCategoriesTable)
        db.execSQL(createBooksTable)
        db.execSQL(createOrdersTable)
        db.execSQL(createOrderItemsTable)
        
        // Insert admin user
        val adminValues = ContentValues().apply {
            put(COLUMN_USERNAME, "admin")
            put(COLUMN_PASSWORD, "admin123")
            put(COLUMN_IS_ADMIN, 1)
        }
        db.insert(TABLE_USERS, null, adminValues)
        
        // Insert sample categories
        val categories = arrayOf("Fiction", "Non-Fiction", "Science", "History", "Technology")
        categories.forEach { category ->
            val values = ContentValues().apply {
                put(COLUMN_CATEGORY_NAME, category)
            }
            db.insert(TABLE_CATEGORIES, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_BOOKS ADD COLUMN $COLUMN_BOOK_IMAGE1 TEXT DEFAULT ''")
            db.execSQL("ALTER TABLE $TABLE_BOOKS ADD COLUMN $COLUMN_BOOK_IMAGE2 TEXT DEFAULT ''")
            db.execSQL("ALTER TABLE $TABLE_BOOKS ADD COLUMN $COLUMN_BOOK_IMAGE3 TEXT DEFAULT ''")
        } else {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_ITEMS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            onCreate(db)
        }
    }
    
    // Ensure database connection is properly managed
    override fun close() {
        // Close any open database object
        val db = writableDatabase
        if (db.isOpen) {
            db.close()
        }
        super.close()
    }
} 