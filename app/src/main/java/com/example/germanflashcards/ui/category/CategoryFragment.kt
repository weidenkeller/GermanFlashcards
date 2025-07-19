package com.example.germanflashcards.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.germanflashcards.databinding.FragmentCategoryBinding
import com.example.germanflashcards.viewmodel.WordViewModel
import com.example.germanflashcards.ui.adapter.CategoryAdapter
import com.example.germanflashcards.data.model.Category
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import android.widget.LinearLayout
import android.widget.Toast
import android.util.Log

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        loadDefaultCategories()
        setupAddCategoryButton()
    }
    
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onCategoryClick = { category ->
                // Переход к выбору типа слов в категории
                val bundle = Bundle().apply {
                    putString("categoryName", category.name)
                    putLong("categoryId", category.id)
                }
                findNavController().navigate(
                    com.example.germanflashcards.R.id.action_categoryFragment_to_categoryWordsFragment,
                    bundle
                )
            },
            onEditClick = { category ->
                showEditCategoryDialog(category)
            }
        )
        
        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collectLatest { categories ->
                // Показываем все категории, а не только 3
                categoryAdapter.submitList(categories)
            }
        }
    }

    private fun loadDefaultCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Проверяем, есть ли уже категории
            val existingCategories = viewModel.getCategoriesSync()
            Log.d("CategoryFragment", "Existing categories: ${existingCategories.map { it.name }}")
            
            if (existingCategories.isEmpty()) {
                // Создаем только 3 дефолтные категории
                val defaultCategories = listOf(
                    Category(name = "IT"),
                    Category(name = "Обычные слова"),
                    Category(name = "Английские слова")
                )
                
                defaultCategories.forEach { category ->
                    viewModel.insertCategory(category)
                }
                Log.d("CategoryFragment", "Created default categories")
            }
        }
    }

    private fun showEditCategoryDialog(category: Category) {
        val dialogView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val editText = TextInputEditText(requireContext()).apply {
            hint = "Название категории"
            setText(category.name)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        dialogView.addView(editText)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Редактировать категорию")
            .setView(dialogView)
            .setPositiveButton("Сохранить", null)
            .setNegativeButton("Отмена", null)
            .setNeutralButton("Удалить", null)
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            val deleteButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL)
            saveButton.setOnClickListener {
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    val updatedCategory = category.copy(name = newName)
                    viewModel.updateCategory(updatedCategory)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Название не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            deleteButton.setOnClickListener {
                showDeleteCategoryDialog(category, dialog)
            }
        }
        dialog.show()
    }

    private fun showDeleteCategoryDialog(category: Category, parentDialog: android.app.Dialog) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить категорию")
            .setMessage("Вы действительно хотите удалить эту категорию?")
            .setPositiveButton("Удалить") { dialog, _ ->
                viewModel.deleteCategory(category)
                dialog.dismiss()
                parentDialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupAddCategoryButton() {
        binding.btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        val editText = TextInputEditText(requireContext()).apply {
            hint = "Название категории"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        dialogView.addView(editText)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Новая категория")
            .setView(dialogView)
            .setPositiveButton("Создать", null)
            .setNegativeButton("Отмена", null)
            .create()
        dialog.setOnShowListener {
            val button = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.insertCategory(com.example.germanflashcards.data.model.Category(name = name))
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Название не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 