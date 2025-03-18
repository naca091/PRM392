package com.example.myapplication.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.CartAdapter
import com.example.myapplication.repository.OrderRepository
import com.example.myapplication.util.CartManager
import com.example.myapplication.util.SessionManager
import java.text.NumberFormat
import java.util.Locale

class CartActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewEmptyCart: TextView
    private lateinit var textViewTotal: TextView
    private lateinit var buttonCheckout: Button
    private lateinit var cartAdapter: CartAdapter
    private lateinit var orderRepository: OrderRepository
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Shopping Cart")
        
        orderRepository = OrderRepository(this)
        sessionManager = SessionManager(this)
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewCart)
        textViewEmptyCart = findViewById(R.id.textViewEmptyCart)
        textViewTotal = findViewById(R.id.textViewTotalAmount)
        buttonCheckout = findViewById(R.id.buttonCheckout)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Load cart items
        loadCartItems()
        
        // Setup checkout button
        buttonCheckout.setOnClickListener {
            placeOrder()
        }
    }
    
    private fun loadCartItems() {
        val cartItems = CartManager.getCartItems()
        
        if (cartItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            textViewEmptyCart.visibility = View.VISIBLE
            buttonCheckout.isEnabled = false
        } else {
            recyclerView.visibility = View.VISIBLE
            textViewEmptyCart.visibility = View.GONE
            buttonCheckout.isEnabled = true
            
            cartAdapter = CartAdapter(cartItems)
            recyclerView.adapter = cartAdapter
            
            updateCartTotal()
        }
    }
    
    private fun updateCartTotal() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        val total = CartManager.getCartTotal()
        textViewTotal.text = "Total: ${currencyFormat.format(total)}"
    }
    
    private fun placeOrder() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login to checkout", Toast.LENGTH_SHORT).show()
            return
        }
        
        val userId = sessionManager.getUserId()
        val cartItems = CartManager.getCartItems()
        
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            // Place order
            val orderId = orderRepository.placeOrder(userId, cartItems)
            
            if (orderId > 0) {
                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
                CartManager.clearCart()
                finish()
            } else {
                Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
} 