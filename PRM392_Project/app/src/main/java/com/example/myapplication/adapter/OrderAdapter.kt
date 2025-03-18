package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Order
import java.text.NumberFormat
import java.util.Locale

class OrderAdapter(
    private var orders: List<Order>,
    private val onItemClickListener: (Order) -> Unit,
    private val isAdmin: Boolean = false,
    private val onStatusUpdateClickListener: ((Order) -> Unit)? = null
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderIdTextView: TextView = view.findViewById(R.id.textViewOrderId)
        val usernameTextView: TextView? = view.findViewById(R.id.textViewOrderUsername)
        val dateTextView: TextView = view.findViewById(R.id.textViewOrderDate)
        val statusTextView: TextView = view.findViewById(R.id.textViewOrderStatus)
        val totalTextView: TextView = view.findViewById(R.id.textViewOrderTotal)
        val updateStatusButton: Button? = view.findViewById(R.id.buttonUpdateStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutId = if (isAdmin) R.layout.item_order_admin else R.layout.item_order
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        
        holder.orderIdTextView.text = "Order #${order.id}"
        holder.usernameTextView?.text = "Customer: ${order.username}"
        holder.dateTextView.text = "Date: ${order.orderDate}"
        holder.statusTextView.text = "Status: ${order.status}"
        holder.totalTextView.text = "Total: ${currencyFormat.format(order.total)}"
        
        holder.itemView.setOnClickListener {
            onItemClickListener(order)
        }
        
        if (isAdmin) {
            holder.updateStatusButton?.setOnClickListener {
                onStatusUpdateClickListener?.invoke(order)
            }
        }
    }

    override fun getItemCount() = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
} 