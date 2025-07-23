package com.example.germanflashcards.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.germanflashcards.databinding.FragmentStatsBinding
import com.example.germanflashcards.viewmodel.WordViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.germanflashcards.R
import com.example.germanflashcards.ui.adapter.WordAdapter
import androidx.navigation.fragment.findNavController

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        binding.cardLearnedWords.setOnClickListener {
            findNavController().navigate(R.id.learnedWordsFragment)
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWordsByStatus(true).collectLatest { learnedWords ->
                binding.tvLearnedWords.text = getString(R.string.learned_words_count, learnedWords.size)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWordsByStatus(false).collectLatest { unlearnedWords ->
                binding.tvUnlearnedWords.text = getString(R.string.unlearned_words_count, unlearnedWords.size)
            }
        }
    }

    private fun showLearnedWordsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_learned_words, null)
        val rv = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvLearnedWords)
        val adapter = WordAdapter(
            onMarkLearned = { /* ничего не делаем, чтобы нельзя было снять галочку */ },
            onEditWord = { /* ничего не делаем, чтобы нельзя было редактировать */ },
            isReadOnly = true
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        // Получаем все выученные слова
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWordsByStatus(true).collectLatest { words ->
                adapter.submitList(words)
            }
        }
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 