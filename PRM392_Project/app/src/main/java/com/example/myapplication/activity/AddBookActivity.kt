package com.example.myapplication.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.model.Book
import com.example.myapplication.model.Category
import com.example.myapplication.repository.BookRepository
import com.example.myapplication.repository.CategoryRepository
import com.example.myapplication.util.SessionManager
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream

class AddBookActivity : BaseActivity() {
    private lateinit var bookRepository: BookRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var categories: List<Category>
    
    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    
    // Biến lưu đường dẫn hình ảnh
    private var imagePath1: String = ""
    private var imagePath2: String = ""
    private var imagePath3: String = ""
    
    // Định nghĩa các hằng số
    private var currentImageSelection = 0 // 1, 2, 3 tương ứng với 3 ảnh cần chọn
    
    // ActivityResultLauncher cho phép chọn ảnh từ thiết bị
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                when (currentImageSelection) {
                    1 -> {
                        imageView1.setImageURI(it)
                        imagePath1 = saveImageToInternalStorage(it, "book_image1_${System.currentTimeMillis()}.jpg")
                    }
                    2 -> {
                        imageView2.setImageURI(it)
                        imagePath2 = saveImageToInternalStorage(it, "book_image2_${System.currentTimeMillis()}.jpg")
                    }
                    3 -> {
                        imageView3.setImageURI(it)
                        imagePath3 = saveImageToInternalStorage(it, "book_image3_${System.currentTimeMillis()}.jpg")
                    }
                }
            }
        }
    }
    
    // Launcher cho Storage Access Framework (SAF)
    private val documentPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                // Tạo URI mới với quyền truy cập lâu dài nếu cần
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
                
                when (currentImageSelection) {
                    1 -> {
                        imageView1.setImageURI(it)
                        imagePath1 = saveImageToInternalStorage(it, "book_image1_${System.currentTimeMillis()}.jpg")
                    }
                    2 -> {
                        imageView2.setImageURI(it)
                        imagePath2 = saveImageToInternalStorage(it, "book_image2_${System.currentTimeMillis()}.jpg")
                    }
                    3 -> {
                        imageView3.setImageURI(it)
                        imagePath3 = saveImageToInternalStorage(it, "book_image3_${System.currentTimeMillis()}.jpg")
                    }
                }
            }
        }
    }
    
    // Launcher để yêu cầu quyền
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            openImagePicker()
        } else {
            // Nếu không được cấp quyền, vẫn có thể truy cập qua SAF
            openDocumentPicker()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupToolbar(toolbar, "Add New Book")
        
        bookRepository = BookRepository(this)
        categoryRepository = CategoryRepository(this)
        sessionManager = SessionManager(this)
        
        // Ensure user is admin
        if (!sessionManager.isAdmin()) {
            finish()
            return
        }
        
        // Get categories for spinner
        categories = categoryRepository.getAllCategories()
        
        // Setup category spinner
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        
        // Khởi tạo các view cho hình ảnh
        imageView1 = findViewById(R.id.imageView1)
        imageView2 = findViewById(R.id.imageView2)
        imageView3 = findViewById(R.id.imageView3)
        
        // Thiết lập sự kiện cho các nút chọn hình ảnh
        findViewById<Button>(R.id.buttonSelectImage1).setOnClickListener {
            currentImageSelection = 1
            checkPermissionAndPickImage()
        }
        
        findViewById<Button>(R.id.buttonSelectImage2).setOnClickListener {
            currentImageSelection = 2
            checkPermissionAndPickImage()
        }
        
        findViewById<Button>(R.id.buttonSelectImage3).setOnClickListener {
            currentImageSelection = 3
            checkPermissionAndPickImage()
        }
        
        // Setup save button
        val buttonSave = findViewById<Button>(R.id.buttonSaveBook)
        buttonSave.setOnClickListener {
            saveBook()
        }
        
        // Setup back button
        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }
    }
    
    private fun checkPermissionAndPickImage() {
        when {
            // Với Android 13+ (API 33+) cần yêu cầu READ_MEDIA_IMAGES
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                val permission = Manifest.permission.READ_MEDIA_IMAGES
                if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    showImagePickerOptions()
                } else {
                    requestPermissionLauncher.launch(arrayOf(permission))
                }
            }
            // Với các phiên bản Android cũ hơn, sử dụng READ_EXTERNAL_STORAGE
            else -> {
                val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    showImagePickerOptions()
                } else {
                    requestPermissionLauncher.launch(arrayOf(permission))
                }
            }
        }
    }
    
    private fun showImagePickerOptions() {
        val options = arrayOf("Thư viện ảnh", "Thiết bị ngoài")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Chọn ảnh từ")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openImagePicker()
                    1 -> openDocumentPicker()
                }
            }
            .show()
    }
    
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }
    
    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            // Thêm External Storage Provider để truy cập thiết bị ngoài
            putExtra(Intent.EXTRA_LOCAL_ONLY, false)
        }
        documentPickerLauncher.launch(intent)
    }
    
    // Lưu hình ảnh vào bộ nhớ trong của ứng dụng để tránh các vấn đề quyền truy cập
    private fun saveImageToInternalStorage(uri: Uri, fileName: String): String {
        val inputStream = contentResolver.openInputStream(uri)
        val directory = File(filesDir, "book_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        
        val file = File(directory, fileName)
        val outputStream = FileOutputStream(file)
        
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        
        return file.absolutePath
    }
    
    private fun saveBook() {
        val editTextTitle = findViewById<TextInputEditText>(R.id.editTextTitle)
        val editTextAuthor = findViewById<TextInputEditText>(R.id.editTextAuthor)
        val editTextPrice = findViewById<TextInputEditText>(R.id.editTextPrice)
        val editTextStock = findViewById<TextInputEditText>(R.id.editTextStock)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        
        val title = editTextTitle.text.toString().trim()
        val author = editTextAuthor.text.toString().trim()
        val priceStr = editTextPrice.text.toString().trim()
        val stockStr = editTextStock.text.toString().trim()
        val categoryPosition = spinnerCategory.selectedItemPosition
        
        // Validate inputs
        if (title.isEmpty() || author.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            val price = priceStr.toDouble()
            val stock = stockStr.toInt()
            
            if (price <= 0) {
                Toast.makeText(this, "Price must be greater than 0", Toast.LENGTH_SHORT).show()
                return
            }
            
            if (stock < 0) {
                Toast.makeText(this, "Stock cannot be negative", Toast.LENGTH_SHORT).show()
                return
            }
            
            if (categoryPosition < 0 || categoryPosition >= categories.size) {
                Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show()
                return
            }
            
            val categoryId = categories[categoryPosition].id
            
            val newBook = Book(
                title = title,
                author = author,
                price = price,
                stock = stock,
                categoryId = categoryId,
                imagePath1 = imagePath1,
                imagePath2 = imagePath2,
                imagePath3 = imagePath3
            )
            
            val bookId = bookRepository.insertBook(newBook)
            
            if (bookId > 0) {
                Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add book", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid price or stock value", Toast.LENGTH_SHORT).show()
        }
    }
} 