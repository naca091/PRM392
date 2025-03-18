package com.example.myapplication.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.OrderItemAdapter
import com.example.myapplication.model.Order
import com.example.myapplication.repository.OrderRepository
import java.text.NumberFormat
import java.util.Locale

class OrderDetailActivity : BaseActivity() {
    private lateinit var textViewOrderId: TextView
    private lateinit var textViewOrderDate: TextView
    private lateinit var textViewOrderStatus: TextView
    private lateinit var textViewOrderTotal: TextView
    private lateinit var recyclerViewOrderItems: RecyclerView
    private lateinit var orderRepository: OrderRepository
    private var orderId: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Order Details")
        
        orderRepository = OrderRepository(this)
        
        // Get order ID from intent
        orderId = intent.getIntExtra("order_id", 0)
        
        if (orderId == 0) {
            finish()
            return
        }
        
        // Initialize views
        textViewOrderId = findViewById(R.id.textViewOrderId)
        textViewOrderDate = findViewById(R.id.textViewOrderDate)
        textViewOrderStatus = findViewById(R.id.textViewOrderStatus)
        textViewOrderTotal = findViewById(R.id.textViewOrderTotal)
        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems)
        
        // Setup Button Back
        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }
        
        // Setup RecyclerView
        recyclerViewOrderItems.layoutManager = LinearLayoutManager(this)
        
        // Load order details
        loadOrderDetails()
    }
    
    private fun loadOrderDetails() {
        val order = orderRepository.getOrderById(orderId)
        
        if (order == null) {
            finish()
            return
        }
        
        displayOrderInfo(order)
        loadOrderItems()
    }
    
    private fun displayOrderInfo(order: Order) {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        
        textViewOrderId.text = "Order #${order.id}"
        textViewOrderDate.text = "Date: ${order.orderDate}"
        textViewOrderStatus.text = "Status: ${order.status}"
        textViewOrderTotal.text = "Total: ${currencyFormat.format(order.total)}"
    }
    
    private fun loadOrderItems() {
        val orderItems = orderRepository.getOrderItems(orderId)
        
        val orderItemAdapter = OrderItemAdapter(orderItems)
        recyclerViewOrderItems.adapter = orderItemAdapter
    }
} 