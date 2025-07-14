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
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWordsByStatus(true).collectLatest { learnedWords ->
                binding.tvLearnedWords.text = "Выучено слов: ${learnedWords.size}"
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWordsByStatus(false).collectLatest { unlearnedWords ->
                binding.tvUnlearnedWords.text = "Не выучено слов: ${unlearnedWords.size}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 