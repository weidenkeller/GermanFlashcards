package com.example.germanflashcards.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.germanflashcards.databinding.FragmentCategoryWordsBinding
import com.example.germanflashcards.viewmodel.WordViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.germanflashcards.ui.adapter.WordAdapter
import com.example.germanflashcards.data.model.Word

class CategoryWordsFragment : Fragment() {
    private var _binding: FragmentCategoryWordsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private var categoryName: String = ""
    private var categoryId: Long = -1L
    private lateinit var wordAdapter: WordAdapter
    private var showLearned: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryWordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Получаем название и id категории из аргументов
        arguments?.let { args ->
            categoryName = args.getString("categoryName", "")
            categoryId = args.getLong("categoryId", -1L)
        }
        
        setupUI()
        setupClickListeners()
        setupRecyclerView()
        setupToggleButtons()
        loadWords()
    }

    private fun setupUI() {
        binding.tvCategoryTitle.text = "Категория: $categoryName"
    }

    private fun setupClickListeners() {
        // Кнопка "Назад"
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        // Удалены лишние обработчики
    }

    private fun setupRecyclerView() {
        wordAdapter = WordAdapter { word ->
            val updatedWord = word.copy(isLearned = !word.isLearned)
            viewModel.updateWord(updatedWord)
        }
        binding.recyclerWords.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wordAdapter
        }
    }

    private fun setupToggleButtons() {
        binding.btnShowUnlearned.setOnClickListener {
            showLearned = false
            updateToggleUI()
            loadWords()
        }
        binding.btnShowLearned.setOnClickListener {
            showLearned = true
            updateToggleUI()
            loadWords()
        }
    }

    private fun updateToggleUI() {
        binding.btnShowUnlearned.isEnabled = showLearned
        binding.btnShowLearned.isEnabled = !showLearned
    }

    private fun loadWords() {
        if (categoryId == -1L) return
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWordsByCategoryAndStatus(categoryId, showLearned).collectLatest { words ->
                wordAdapter.submitList(words)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 