package com.example.myapplication.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.R

/**
 * Base Activity cung cấp chức năng chung cho tất cả các Activity
 * bao gồm cài đặt nút Back và xử lý sự kiện quay lại
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    
    /**
     * Cài đặt toolbar với nút Back
     * @param toolbar Toolbar cần cài đặt
     * @param title Tiêu đề hiển thị trên toolbar
     * @param showBackButton True nếu muốn hiển thị nút Back
     */
    protected fun setupToolbar(toolbar: Toolbar, title: String, showBackButton: Boolean = true) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        
        if (showBackButton) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
} 