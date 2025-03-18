package com.example.myapplication.util

import com.example.myapplication.model.Book
import com.example.myapplication.model.CartItem

object CartManager {
    private val cartItems = mutableListOf<CartItem>()
    
    fun getCartItems(): List<CartItem> {
        return cartItems.toList()
    }
    
    fun addToCart(book: Book) {
        val existingItem = cartItems.find { it.book.id == book.id }
        
        if (existingItem != null) {
            // If book is already in cart, increase quantity if there's enough stock
            if (existingItem.quantity < book.stock) {
                existingItem.quantity++
            }
        } else {
            // Add new item to cart if there's stock available
            if (book.stock > 0) {
                cartItems.add(CartItem(book))
            }
        }
    }
    
    fun addToCart(cartItem: CartItem) {
        val book = cartItem.book
        val existingItem = cartItems.find { it.book.id == book.id }
        
        if (existingItem != null) {
            // If book is already in cart, update quantity if there's enough stock
            val newQuantity = existingItem.quantity + cartItem.quantity
            if (newQuantity <= book.stock) {
                existingItem.quantity = newQuantity
            } else {
                existingItem.quantity = book.stock // Limit to max stock
            }
        } else {
            // Add new item to cart if there's stock available
            if (book.stock > 0) {
                // Ensure quantity doesn't exceed stock
                val quantity = minOf(cartItem.quantity, book.stock)
                cartItems.add(CartItem(book, quantity))
            }
        }
    }
    
    fun removeFromCart(bookId: Int) {
        cartItems.removeIf { it.book.id == bookId }
    }
    
    fun updateQuantity(bookId: Int, quantity: Int) {
        val cartItem = cartItems.find { it.book.id == bookId }
        
        cartItem?.let {
            // Make sure the quantity does not exceed the stock
            if (quantity <= it.book.stock && quantity > 0) {
                it.quantity = quantity
            }
        }
    }
    
    fun getCartTotal(): Double {
        return cartItems.sumOf { it.getSubtotal() }
    }
    
    fun getItemCount(): Int {
        return cartItems.sumOf { it.quantity }
    }
    
    fun clearCart() {
        cartItems.clear()
    }
    
    fun isBookInCart(bookId: Int): Boolean {
        return cartItems.any { it.book.id == bookId }
    }
} 