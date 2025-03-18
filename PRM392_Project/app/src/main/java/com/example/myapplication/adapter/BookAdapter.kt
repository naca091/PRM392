package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activity.BookDetailActivity
import com.example.myapplication.model.Book
import com.example.myapplication.util.CartManager
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class BookAdapter(
    private val context: Context,
    private var books: List<Book>,
    private val isAdmin: Boolean = false,
    private val onItemClickListener: (Book) -> Unit,
    private val onEditClickListener: ((Book) -> Unit)? = null,
    private val onDeleteClickListener: ((Book) -> Unit)? = null
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewBook: ImageView = view.findViewById(R.id.imageViewBook)
        val titleTextView: TextView = view.findViewById(R.id.textViewBookTitle)
        val authorTextView: TextView = view.findViewById(R.id.textViewBookAuthor)
        val priceTextView: TextView = view.findViewById(R.id.textViewBookPrice)
        val categoryTextView: TextView = view.findViewById(R.id.textViewBookCategory)
        val stockTextView: TextView = view.findViewById(R.id.textViewBookStock)
        val addToCartButton: Button = view.findViewById(R.id.buttonAddToCart)
        val editButton: Button? = view.findViewById(R.id.buttonEditBook)
        val deleteButton: Button? = view.findViewById(R.id.buttonDeleteBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val layoutId = if (isAdmin) R.layout.item_book_admin else R.layout.item_book
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        
        // Load book image
        loadBookImage(book.imagePath1, holder.imageViewBook)
        
        holder.titleTextView.text = book.title
        holder.authorTextView.text = book.author
        holder.priceTextView.text = currencyFormat.format(book.price)
        holder.categoryTextView.text = book.categoryName
        holder.stockTextView.text = "In Stock: ${book.stock}"
        
        if (!isAdmin) {
            // For user view
            holder.itemView.setOnClickListener {
                onItemClickListener(book)
            }
            
            if (book.stock > 0) {
                holder.addToCartButton.isEnabled = true
                holder.addToCartButton.text = if (CartManager.isBookInCart(book.id)) "Already in Cart" else "Add to Cart"
                
                if (!CartManager.isBookInCart(book.id)) {
                    holder.addToCartButton.setOnClickListener {
                        CartManager.addToCart(book)
                        notifyItemChanged(position)
                    }
                } else {
                    holder.addToCartButton.setOnClickListener(null)
                }
            } else {
                holder.addToCartButton.isEnabled = false
                holder.addToCartButton.text = "Out of Stock"
            }
        } else {
            // For admin view
            holder.itemView.setOnClickListener {
                onItemClickListener(book)
            }
            
            holder.editButton?.setOnClickListener {
                onEditClickListener?.invoke(book)
            }
            
            holder.deleteButton?.setOnClickListener {
                onDeleteClickListener?.invoke(book)
            }
        }
    }

    private fun loadBookImage(imagePath: String?, imageView: ImageView) {
        if (imagePath.isNullOrEmpty()) {
            // Set default image if no image path is available
            imageView.setImageResource(R.drawable.ic_book)
            return
        }
        
        val imageFile = File(imagePath)
        if (imageFile.exists()) {
            try {
                // Load the image from file
                val bitmap: Bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                // Handle error loading image
                imageView.setImageResource(R.drawable.ic_book)
            }
        } else {
            // Set default image if file doesn't exist
            imageView.setImageResource(R.drawable.ic_book)
        }
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
} 