package com.example.myapplication.activity

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.OrderAdapter
import com.example.myapplication.repository.OrderRepository
import com.example.myapplication.util.SessionManager

class OrderHistoryActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoOrders: TextView
    private lateinit var orderRepository: OrderRepository
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Order History")
        
        orderRepository = OrderRepository(this)
        sessionManager = SessionManager(this)
        
        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewOrders)
        textViewNoOrders = findViewById(R.id.textViewNoOrders)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Load orders
        loadOrders()
    }
    
    private fun loadOrders() {
        val userId = sessionManager.getUserId()
        val orders = orderRepository.getOrdersByUserId(userId)
        
        if (orders.isEmpty()) {
            recyclerView.visibility = View.GONE
            textViewNoOrders.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            textViewNoOrders.visibility = View.GONE
            
            val orderAdapter = OrderAdapter(
                orders = orders,
                onItemClickListener = { order ->
                    val intent = Intent(this, OrderDetailActivity::class.java)
                    intent.putExtra("order_id", order.id)
                    startActivity(intent)
                },
                isAdmin = false
            )
            
            recyclerView.adapter = orderAdapter
        }
    }
} 