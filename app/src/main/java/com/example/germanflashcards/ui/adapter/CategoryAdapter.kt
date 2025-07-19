package com.example.germanflashcards.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.germanflashcards.data.model.Category
import com.example.germanflashcards.databinding.ItemCategoryBinding
import android.util.Log

class CategoryAdapter(
    private val onCategoryClick: (Category) -> Unit,
    private val onEditClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding, onCategoryClick, onEditClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
        private val onCategoryClick: (Category) -> Unit,
        private val onEditClick: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            // Отображаем название категории
            binding.tvCategoryName.text = category.name
            
            // Отладочная информация
            Log.d("CategoryAdapter", "Setting category name: '${category.name}' for category ID: ${category.id}")
            
            // Обработчик клика по всей карточке
            binding.root.setOnClickListener {
                onCategoryClick(category)
            }
            
            // Обработчик клика по кнопке редактирования
            binding.btnEditCategory.setOnClickListener {
                onEditClick(category)
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
} 