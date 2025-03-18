package com.example.myapplication.model

data class Book(
    val id: Int = 0,
    val title: String,
    val author: String,
    val price: Double,
    val categoryId: Int,
    val stock: Int,
    var categoryName: String = "", // This field is for UI display purposes
    val imagePath1: String = "", // Đường dẫn đến hình ảnh 1
    val imagePath2: String = "", // Đường dẫn đến hình ảnh 2
    val imagePath3: String = ""  // Đường dẫn đến hình ảnh 3
) 