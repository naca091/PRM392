package com.example.myapplication.repository

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.database.BookstoreDbHelper
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_BOOK_STOCK
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_BOOK_TITLE
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ID
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ORDER_DATE
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ORDER_ITEM_BOOK_ID
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ORDER_ITEM_ORDER_ID
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ORDER_ITEM_PRICE
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ORDER_ITEM_QUANTITY
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ORDER_STATUS
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ORDER_TOTAL
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_ORDER_USER_ID
import com.example.myapplication.database.BookstoreDbHelper.Companion.COLUMN_USERNAME
import com.example.myapplication.database.BookstoreDbHelper.Companion.TABLE_BOOKS
import com.example.myapplication.database.BookstoreDbHelper.Companion.TABLE_ORDERS
import com.example.myapplication.database.BookstoreDbHelper.Companion.TABLE_ORDER_ITEMS
import com.example.myapplication.database.BookstoreDbHelper.Companion.TABLE_USERS
import com.example.myapplication.model.Book
import com.example.myapplication.model.CartItem
import com.example.myapplication.model.Order
import com.example.myapplication.model.OrderItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderRepository(context: Context) {
    private val dbHelper = BookstoreDbHelper(context)
    private val bookRepository = BookRepository(context)

    fun getAllOrders(): List<Order> {
        val orders = mutableListOf<Order>()
        val db = dbHelper.readableDatabase
        
        val query = """
            SELECT o.*, u.$COLUMN_USERNAME
            FROM $TABLE_ORDERS o
            JOIN $TABLE_USERS u ON o.$COLUMN_ORDER_USER_ID = u.$COLUMN_ID
            ORDER BY o.$COLUMN_ORDER_DATE DESC
        """.trimIndent()
        
        val cursor = db.rawQuery(query, null)
        
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val userId = getInt(getColumnIndexOrThrow(COLUMN_ORDER_USER_ID))
                val orderDate = getString(getColumnIndexOrThrow(COLUMN_ORDER_DATE))
                val status = getString(getColumnIndexOrThrow(COLUMN_ORDER_STATUS))
                val total = getDouble(getColumnIndexOrThrow(COLUMN_ORDER_TOTAL))
                val username = getString(getColumnIndexOrThrow(COLUMN_USERNAME))
                
                orders.add(Order(id, userId, orderDate, status, total, username))
            }
        }
        
        cursor.close()
        return orders
    }
    
    fun getOrdersByUserId(userId: Int): List<Order> {
        val orders = mutableListOf<Order>()
        val db = dbHelper.readableDatabase
        
        val query = """
            SELECT o.*, u.$COLUMN_USERNAME
            FROM $TABLE_ORDERS o
            JOIN $TABLE_USERS u ON o.$COLUMN_ORDER_USER_ID = u.$COLUMN_ID
            WHERE o.$COLUMN_ORDER_USER_ID = ?
            ORDER BY o.$COLUMN_ORDER_DATE DESC
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val uId = getInt(getColumnIndexOrThrow(COLUMN_ORDER_USER_ID))
                val orderDate = getString(getColumnIndexOrThrow(COLUMN_ORDER_DATE))
                val status = getString(getColumnIndexOrThrow(COLUMN_ORDER_STATUS))
                val total = getDouble(getColumnIndexOrThrow(COLUMN_ORDER_TOTAL))
                val username = getString(getColumnIndexOrThrow(COLUMN_USERNAME))
                
                orders.add(Order(id, uId, orderDate, status, total, username))
            }
        }
        
        cursor.close()
        return orders
    }
    
    fun getOrderById(orderId: Int): Order? {
        val db = dbHelper.readableDatabase
        
        val query = """
            SELECT o.*, u.$COLUMN_USERNAME
            FROM $TABLE_ORDERS o
            JOIN $TABLE_USERS u ON o.$COLUMN_ORDER_USER_ID = u.$COLUMN_ID
            WHERE o.$COLUMN_ID = ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(orderId.toString()))
        
        var order: Order? = null
        
        with(cursor) {
            if (moveToFirst()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val userId = getInt(getColumnIndexOrThrow(COLUMN_ORDER_USER_ID))
                val orderDate = getString(getColumnIndexOrThrow(COLUMN_ORDER_DATE))
                val status = getString(getColumnIndexOrThrow(COLUMN_ORDER_STATUS))
                val total = getDouble(getColumnIndexOrThrow(COLUMN_ORDER_TOTAL))
                val username = getString(getColumnIndexOrThrow(COLUMN_USERNAME))
                
                order = Order(id, userId, orderDate, status, total, username)
            }
        }
        
        cursor.close()
        return order
    }
    
    fun getOrderItems(orderId: Int): List<OrderItem> {
        val orderItems = mutableListOf<OrderItem>()
        val db = dbHelper.readableDatabase
        
        val query = """
            SELECT oi.*, b.$COLUMN_BOOK_TITLE
            FROM $TABLE_ORDER_ITEMS oi
            JOIN $TABLE_BOOKS b ON oi.$COLUMN_ORDER_ITEM_BOOK_ID = b.$COLUMN_ID
            WHERE oi.$COLUMN_ORDER_ITEM_ORDER_ID = ?
            ORDER BY oi.$COLUMN_ID ASC
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(orderId.toString()))
        
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val orderIdFromDB = getInt(getColumnIndexOrThrow(COLUMN_ORDER_ITEM_ORDER_ID))
                val bookId = getInt(getColumnIndexOrThrow(COLUMN_ORDER_ITEM_BOOK_ID))
                val quantity = getInt(getColumnIndexOrThrow(COLUMN_ORDER_ITEM_QUANTITY))
                val price = getDouble(getColumnIndexOrThrow(COLUMN_ORDER_ITEM_PRICE))
                val bookTitle = getString(getColumnIndexOrThrow(COLUMN_BOOK_TITLE))
                
                orderItems.add(OrderItem(id, orderIdFromDB, bookId, quantity, price, bookTitle))
            }
        }
        
        cursor.close()
        return orderItems
    }
    
    fun placeOrder(userId: Int, cartItems: List<CartItem>): Long {
        val db = dbHelper.writableDatabase
        
        try {
            db.beginTransaction()
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            
            // Calculate total
            val total = cartItems.sumOf { it.getSubtotal() }
            
            // Create order
            val orderValues = ContentValues().apply {
                put(COLUMN_ORDER_USER_ID, userId)
                put(COLUMN_ORDER_DATE, currentDate)
                put(COLUMN_ORDER_STATUS, "Pending")
                put(COLUMN_ORDER_TOTAL, total)
            }
            
            val orderId = db.insert(TABLE_ORDERS, null, orderValues)
            
            // Create order items
            cartItems.forEach { cartItem ->
                val orderItemValues = ContentValues().apply {
                    put(COLUMN_ORDER_ITEM_ORDER_ID, orderId)
                    put(COLUMN_ORDER_ITEM_BOOK_ID, cartItem.book.id)
                    put(COLUMN_ORDER_ITEM_QUANTITY, cartItem.quantity)
                    put(COLUMN_ORDER_ITEM_PRICE, cartItem.book.price)
                }
                
                db.insert(TABLE_ORDER_ITEMS, null, orderItemValues)
                
                // Update book stock directly in this transaction
                val newStock = cartItem.book.stock - cartItem.quantity
                val stockValues = ContentValues().apply {
                    put(COLUMN_BOOK_STOCK, newStock)
                }
                
                db.update(
                    TABLE_BOOKS,
                    stockValues,
                    "$COLUMN_ID = ?",
                    arrayOf(cartItem.book.id.toString())
                )
            }
            
            db.setTransactionSuccessful()
            return orderId
        } finally {
            db.endTransaction()
        }
    }
    
    fun updateOrderStatus(orderId: Int, newStatus: String): Int {
        val db = dbHelper.writableDatabase
        
        val values = ContentValues().apply {
            put(COLUMN_ORDER_STATUS, newStatus)
        }
        
        return db.update(
            TABLE_ORDERS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(orderId.toString())
        )
    }
} 