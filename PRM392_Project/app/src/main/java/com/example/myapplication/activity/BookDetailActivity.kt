package com.example.myapplication.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.R
import com.example.myapplication.model.Book
import com.example.myapplication.model.CartItem
import com.example.myapplication.repository.BookRepository
import com.example.myapplication.util.CartManager
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class BookDetailActivity : BaseActivity() {
    private lateinit var bookRepository: BookRepository
    private lateinit var book: Book
    private var quantity: Int = 1
    private var isAdmin: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Book Details")
        
        bookRepository = BookRepository(this)
        
        val bookId = intent.getIntExtra("book_id", -1)
        isAdmin = intent.getBooleanExtra("is_admin", false)
        
        if (bookId == -1) {
            Toast.makeText(this, "Invalid book ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        book = bookRepository.getBookById(bookId) ?: run {
            Toast.makeText(this, "Book not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        displayBookDetails()
        loadBookImages()
        setupQuantityButtons()
        setupAddToCartButton()
        
        if (isAdmin) {
            setupAdminButtons()
        }
    }
    
    private fun displayBookDetails() {
        val textViewBookTitle = findViewById<TextView>(R.id.textViewBookTitle)
        val textViewBookAuthor = findViewById<TextView>(R.id.textViewBookAuthor)
        val textViewBookCategory = findViewById<TextView>(R.id.textViewBookCategory)
        val textViewBookPrice = findViewById<TextView>(R.id.textViewBookPrice)
        val textViewBookStock = findViewById<TextView>(R.id.textViewBookStock)
        
        textViewBookTitle.text = book.title
        textViewBookAuthor.text = "Author: ${book.author}"
        textViewBookCategory.text = "Category: ${bookRepository.getCategoryNameById(book.categoryId)}"
        
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        textViewBookPrice.text = "Price: ${currencyFormat.format(book.price)}"
        
        textViewBookStock.text = "In Stock: ${book.stock}"
    }
    
    private fun loadBookImages() {
        val imageView1 = findViewById<ImageView>(R.id.imageViewBook1)
        val imageView2 = findViewById<ImageView>(R.id.imageViewBook2)
        val imageView3 = findViewById<ImageView>(R.id.imageViewBook3)
        
        // Load first image
        loadImageFromPath(book.imagePath1, imageView1)
        
        // Load second image if available
        if (!book.imagePath2.isNullOrEmpty()) {
            loadImageFromPath(book.imagePath2, imageView2)
        }
        
        // Load third image if available
        if (!book.imagePath3.isNullOrEmpty()) {
            loadImageFromPath(book.imagePath3, imageView3)
        }
    }
    
    private fun loadImageFromPath(imagePath: String?, imageView: ImageView) {
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
    
    private fun setupQuantityButtons() {
        val textViewQuantity = findViewById<TextView>(R.id.textViewQuantity)
        val buttonDecreaseQuantity = findViewById<Button>(R.id.buttonDecreaseQuantity)
        val buttonIncreaseQuantity = findViewById<Button>(R.id.buttonIncreaseQuantity)
        
        buttonDecreaseQuantity.setOnClickListener {
            if (quantity > 1) {
                quantity--
                textViewQuantity.text = quantity.toString()
            }
        }
        
        buttonIncreaseQuantity.setOnClickListener {
            if (quantity < book.stock) {
                quantity++
                textViewQuantity.text = quantity.toString()
            } else {
                Toast.makeText(this, "Cannot exceed available stock", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupAddToCartButton() {
        val buttonAddToCart = findViewById<Button>(R.id.buttonAddToCart)
        
        buttonAddToCart.setOnClickListener {
            if (book.stock <= 0) {
                Toast.makeText(this, "This book is out of stock", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val cartItem = CartItem(book, quantity)
            CartManager.addToCart(cartItem)
            
            Toast.makeText(this, "${book.title} added to cart", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupAdminButtons() {
        val layoutAdminButtons = findViewById<View>(R.id.layoutAdminButtons)
        layoutAdminButtons.visibility = View.VISIBLE
        
        val buttonEditBook = findViewById<Button>(R.id.buttonEditBook)
        val buttonDeleteBook = findViewById<Button>(R.id.buttonDeleteBook)
        
        buttonEditBook.setOnClickListener {
            // Navigate to edit book screen
            Toast.makeText(this, "Edit book functionality to be implemented", Toast.LENGTH_SHORT).show()
        }
        
        buttonDeleteBook.setOnClickListener {
            bookRepository.deleteBook(book.id)
            Toast.makeText(this, "${book.title} deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 