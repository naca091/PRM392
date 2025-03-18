package com.example.myapplication.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.myapplication.database.BookstoreDbHelper
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ID
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_IS_ADMIN
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_PASSWORD
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_USERNAME
import com.example.myapplication.database.BookstoreDbHelper.Companion.TABLE_USERS
import com.example.myapplication.model.User

class UserRepository(context: Context) {
    private val dbHelper = BookstoreDbHelper(context)
    
    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = dbHelper.readableDatabase
        
        val cursor = db.query(
            TABLE_USERS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_USERNAME ASC"
        )
        
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val username = getString(getColumnIndexOrThrow(COLUMN_USERNAME))
                val password = getString(getColumnIndexOrThrow(COLUMN_PASSWORD))
                val isAdmin = getInt(getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
                
                users.add(User(id, username, password, isAdmin))
            }
        }
        
        cursor.close()
        return users
    }
    
    fun getUserById(userId: Int): User? {
        val db = dbHelper.readableDatabase
        
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )
        
        var user: User? = null
        
        with(cursor) {
            if (moveToFirst()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val username = getString(getColumnIndexOrThrow(COLUMN_USERNAME))
                val password = getString(getColumnIndexOrThrow(COLUMN_PASSWORD))
                val isAdmin = getInt(getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
                
                user = User(id, username, password, isAdmin)
            }
        }
        
        cursor.close()
        return user
    }
    
    fun getUserByUsername(username: String): User? {
        val db = dbHelper.readableDatabase
        
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )
        
        var user: User? = null
        
        with(cursor) {
            if (moveToFirst()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val fetchedUsername = getString(getColumnIndexOrThrow(COLUMN_USERNAME))
                val password = getString(getColumnIndexOrThrow(COLUMN_PASSWORD))
                val isAdmin = getInt(getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
                
                user = User(id, fetchedUsername, password, isAdmin)
            }
        }
        
        cursor.close()
        return user
    }
    
    fun insertUser(user: User): Long {
        val db = dbHelper.writableDatabase
        
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_PASSWORD, user.password)
            put(COLUMN_IS_ADMIN, if (user.isAdmin) 1 else 0)
        }
        
        return db.insert(TABLE_USERS, null, values)
    }
    
    fun updateUser(user: User): Int {
        val db = dbHelper.writableDatabase
        
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_PASSWORD, user.password)
            put(COLUMN_IS_ADMIN, if (user.isAdmin) 1 else 0)
        }
        
        return db.update(
            TABLE_USERS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(user.id.toString())
        )
    }
    
    fun deleteUser(userId: Int): Int {
        val db = dbHelper.writableDatabase
        
        return db.delete(
            TABLE_USERS,
            "$COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
    }
} 