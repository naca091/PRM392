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
import com.example.myapplication.util.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar

class AdminActivity : BaseActivity() {
    private lateinit var bookRepository: BookRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Reusing the main layout
        
        bookRepository = BookRepository(this)
        sessionManager = SessionManager(this)
        
        // Ensure user is admin
        if (!sessionManager.isAdmin()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Admin Dashboard", false) // Không cần nút Back ở màn hình chính
        
        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewBooks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Setup FAB for adding books
        val fab = findViewById<FloatingActionButton>(R.id.fabAddBook)
        fab.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
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
                intent.putExtra("is_admin", true)
                startActivity(intent)
            },
            isAdmin = true,
            onEditClickListener = { book ->
                val intent = Intent(this, EditBookActivity::class.java)
                intent.putExtra("book_id", book.id)
                startActivity(intent)
            },
            onDeleteClickListener = { book ->
                deleteBook(book)
            }
        )
        recyclerView.adapter = bookAdapter
    }
    
    private fun deleteBook(book: Book) {
        bookRepository.deleteBook(book.id)
        loadBooks() // Refresh list after deletion
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        
        // Setup search
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
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
            R.id.action_categories -> {
                startActivity(Intent(this, CategoryActivity::class.java))
                true
            }
            R.id.action_users -> {
                startActivity(Intent(this, UserManagementActivity::class.java))
                true
            }
            R.id.action_orders -> {
                startActivity(Intent(this, OrderManagementActivity::class.java))
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