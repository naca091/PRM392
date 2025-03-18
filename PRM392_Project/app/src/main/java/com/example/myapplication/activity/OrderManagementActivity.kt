package com.example.myapplication.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.OrderAdapter
import com.example.myapplication.model.Order
import com.example.myapplication.repository.OrderRepository
import com.example.myapplication.util.SessionManager

class OrderManagementActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoOrders: TextView
    private lateinit var orderRepository: OrderRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var orderAdapter: OrderAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_management)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Order Management")
        
        orderRepository = OrderRepository(this)
        sessionManager = SessionManager(this)
        
        // Ensure user is admin
        if (!sessionManager.isAdmin()) {
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
        val orders = orderRepository.getAllOrders()
        
        if (orders.isEmpty()) {
            recyclerView.visibility = View.GONE
            textViewNoOrders.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            textViewNoOrders.visibility = View.GONE
            
            orderAdapter = OrderAdapter(
                orders = orders,
                onItemClickListener = { order ->
                    val intent = Intent(this, OrderDetailActivity::class.java)
                    intent.putExtra("order_id", order.id)
                    startActivity(intent)
                },
                isAdmin = true,
                onStatusUpdateClickListener = { order ->
                    showStatusUpdateDialog(order)
                }
            )
            
            recyclerView.adapter = orderAdapter
        }
    }
    
    private fun showStatusUpdateDialog(order: Order) {
        val statuses = arrayOf("Pending", "Processing", "Shipped", "Delivered", "Cancelled")
        
        AlertDialog.Builder(this)
            .setTitle("Update Order Status")
            .setItems(statuses) { _, which ->
                val newStatus = statuses[which]
                updateOrderStatus(order.id, newStatus)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun updateOrderStatus(orderId: Int, newStatus: String) {
        orderRepository.updateOrderStatus(orderId, newStatus)
        loadOrders() // Refresh the order list
    }
    
    override fun onResume() {
        super.onResume()
        loadOrders() // Refresh orders when returning to this activity
    }
}
