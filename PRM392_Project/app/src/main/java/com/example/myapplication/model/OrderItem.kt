package com.example.myapplication.model

data class OrderItem(
    val id: Int = 0,
    val orderId: Int,
    val bookId: Int,
    val quantity: Int,
    val price: Double,
    var bookTitle: String = "" // For UI display purposes
) 