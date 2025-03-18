package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.CartItem
import com.example.myapplication.util.CartManager
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private var cartItems: List<CartItem>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.textViewCartItemTitle)
        val authorTextView: TextView = view.findViewById(R.id.textViewCartItemAuthor)
        val priceTextView: TextView = view.findViewById(R.id.textViewCartItemPrice)
        val quantityTextView: TextView = view.findViewById(R.id.textViewCartItemQuantity)
        val subtotalTextView: TextView = view.findViewById(R.id.textViewCartItemSubtotal)
        val decreaseButton: ImageButton = view.findViewById(R.id.buttonDecreaseQuantity)
        val increaseButton: ImageButton = view.findViewById(R.id.buttonIncreaseQuantity)
        val removeButton: Button = view.findViewById(R.id.buttonRemoveFromCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val book = cartItem.book
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        
        holder.titleTextView.text = book.title
        holder.authorTextView.text = book.author
        holder.priceTextView.text = currencyFormat.format(book.price)
        holder.quantityTextView.text = cartItem.quantity.toString()
        holder.subtotalTextView.text = currencyFormat.format(cartItem.getSubtotal())
        
        holder.decreaseButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                CartManager.updateQuantity(book.id, cartItem.quantity - 1)
                notifyItemChanged(position)
            }
        }
        
        holder.increaseButton.setOnClickListener {
            if (cartItem.quantity < book.stock) {
                CartManager.updateQuantity(book.id, cartItem.quantity + 1)
                notifyItemChanged(position)
            }
        }
        
        holder.removeButton.setOnClickListener {
            CartManager.removeFromCart(book.id)
            updateCartItems(CartManager.getCartItems())
        }
    }

    override fun getItemCount() = cartItems.size

    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
} 