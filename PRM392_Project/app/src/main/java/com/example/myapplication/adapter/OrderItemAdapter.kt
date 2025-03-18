package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.OrderItem
import java.text.NumberFormat
import java.util.Locale

class OrderItemAdapter(
    private var orderItems: List<OrderItem>
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.textViewOrderItemTitle)
        val priceTextView: TextView = view.findViewById(R.id.textViewOrderItemPrice)
        val quantityTextView: TextView = view.findViewById(R.id.textViewOrderItemQuantity)
        val subtotalTextView: TextView = view.findViewById(R.id.textViewOrderItemSubtotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_item, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val orderItem = orderItems[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        
        holder.titleTextView.text = orderItem.bookTitle
        holder.priceTextView.text = currencyFormat.format(orderItem.price)
        holder.quantityTextView.text = "Quantity: ${orderItem.quantity}"
        
        val subtotal = orderItem.price * orderItem.quantity
        holder.subtotalTextView.text = "Subtotal: ${currencyFormat.format(subtotal)}"
    }

    override fun getItemCount() = orderItems.size

    fun updateOrderItems(newOrderItems: List<OrderItem>) {
        orderItems = newOrderItems
        notifyDataSetChanged()
    }
} 