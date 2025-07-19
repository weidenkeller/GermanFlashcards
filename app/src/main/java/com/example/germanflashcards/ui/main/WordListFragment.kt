package com.example.germanflashcards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.germanflashcards.R
import com.example.germanflashcards.databinding.FragmentWordListBinding
import com.example.germanflashcards.ui.adapter.WordAdapter
import com.example.germanflashcards.viewmodel.WordViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

class WordListFragment : Fragment() {
    private var _binding: FragmentWordListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private lateinit var wordAdapter: WordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupListeners()
        setupLanguageSwitcher()
    }
    
    private fun setupRecyclerView() {
        wordAdapter = WordAdapter { word ->
            val updatedWord = word.copy(isLearned = !word.isLearned)
            viewModel.updateWord(updatedWord)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wordAdapter
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWordsByCategory(1).collectLatest { words ->
                wordAdapter.submitList(words)
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnAddWord.setOnClickListener {
            findNavController().navigate(R.id.action_to_addWord)
        }
    }

    private val languages = listOf("ru", "de", "en")
    private fun setupLanguageSwitcher() {
        val currentLang = getCurrentLang()
        updateLangSwitcher(currentLang)
        binding.langSwitcherBlock.setOnClickListener {
            val nextLang = getNextLang(getCurrentLang())
            setLocale(nextLang)
            updateLangSwitcher(nextLang)
        }
    }
    private fun updateLangSwitcher(lang: String) {
        val label = when(lang) {
            "ru" -> "RU"
            "de" -> "DE"
            "en" -> "EN"
            else -> lang.uppercase()
        }
        binding.tvLangSwitcher.text = label
    }
    private fun getCurrentLang(): String {
        return Locale.getDefault().language
    }
    private fun getNextLang(current: String): String {
        val idx = languages.indexOf(current)
        return languages[(idx + 1) % languages.size]
    }
    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}