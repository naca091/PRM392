package com.example.myapplication.model

data class Order(
    val id: Int = 0,
    val userId: Int,
    val orderDate: String,
    val status: String,
    val total: Double,
    var username: String = "" // For UI display purposes
) 