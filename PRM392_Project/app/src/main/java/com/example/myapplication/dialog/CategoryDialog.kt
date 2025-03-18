package com.example.myapplication.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.R
import com.example.myapplication.model.Category

class CategoryDialog(context: Context, private var category: Category? = null) : Dialog(context) {
    private var onCategoryAddedListener: (() -> Unit)? = null
    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_category)

        editText = findViewById(R.id.editTextCategoryName)
        category?.let {
            editText.setText(it.name)
        }

        findViewById<android.widget.Button>(R.id.buttonSave).setOnClickListener {
            val categoryName = editText.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                onCategoryAddedListener?.invoke()
                dismiss()
            }
        }

        findViewById<android.widget.Button>(R.id.buttonCancel).setOnClickListener {
            dismiss()
        }
    }

    fun setOnCategoryAddedListener(listener: () -> Unit) {
        onCategoryAddedListener = listener
    }

    fun getCategoryName(): String {
        return editText.text.toString().trim()
    }
} 