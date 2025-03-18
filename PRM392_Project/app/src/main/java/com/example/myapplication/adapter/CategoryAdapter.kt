package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onEditClickListener: (Category) -> Unit,
    private val onDeleteClickListener: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.textViewCategoryName)
        val buttonEdit: ImageButton = view.findViewById(R.id.buttonEdit)
        val buttonDelete: ImageButton = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.textViewName.text = category.name

        holder.buttonEdit.setOnClickListener {
            onEditClickListener(category)
        }

        holder.buttonDelete.setOnClickListener {
            onDeleteClickListener(category)
        }
    }

    override fun getItemCount() = categories.size

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
} 