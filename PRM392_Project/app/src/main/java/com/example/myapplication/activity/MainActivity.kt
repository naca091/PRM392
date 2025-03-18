package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.BookAdapter
import com.example.myapplication.model.Book
import com.example.myapplication.repository.BookRepository
import com.example.myapplication.util.CartManager
import com.example.myapplication.util.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar

class MainActivity : BaseActivity() {
    private lateinit var bookRepository: BookRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private lateinit var searchView: SearchView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        bookRepository = BookRepository(this)
        sessionManager = SessionManager(this)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Bookstore", false) // Không hiển thị nút Back ở màn hình chính
        
        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewBooks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Hide FAB for regular users
        val fab = findViewById<FloatingActionButton>(R.id.fabAddBook)
        fab.hide()
        
        // Add sample books if none exist
        val books = bookRepository.getAllBooks()
        if (books.isEmpty()) {
            bookRepository.addSampleBooks()
        }
        
        // Load books
        loadBooks()
    }
    
    private fun loadBooks() {
        val books = bookRepository.getAllBooks()
        bookAdapter = BookAdapter(
            context = this,
            books = books,
            onItemClickListener = { book ->
                val intent = Intent(this, BookDetailActivity::class.java)
                intent.putExtra("book_id", book.id)
                startActivity(intent)
            },
            isAdmin = false
        )
        recyclerView.adapter = bookAdapter
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        
        // Setup search
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchBooks(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    loadBooks()
                }
                return true
            }
        })
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                startActivity(Intent(this, CartActivity::class.java))
                true
            }
            R.id.action_orders -> {
                startActivity(Intent(this, OrderHistoryActivity::class.java))
                true
            }
            R.id.action_logout -> {
                sessionManager.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun searchBooks(query: String) {
        val searchResults = bookRepository.searchBooks(query)
        bookAdapter.updateBooks(searchResults)
    }
    
    override fun onResume() {
        super.onResume()
        loadBooks() // Refresh books when returning to this activity
    }
} 