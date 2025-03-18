package com.example.myapplication.model

data class CartItem(
    val book: Book,
    var quantity: Int = 1
) {
    fun getSubtotal(): Double {
        return book.price * quantity
    }
} 