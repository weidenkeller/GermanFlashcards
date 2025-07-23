package com.example.germanflashcards.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.germanflashcards.databinding.FragmentLearnedWordsBinding
import com.example.germanflashcards.viewmodel.WordViewModel
import com.example.germanflashcards.ui.adapter.WordAdapter
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LearnedWordsFragment : Fragment() {
    private var _binding: FragmentLearnedWordsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnedWordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = WordAdapter(
            onMarkLearned = { /* ничего не делаем */ },
            onEditWord = { /* ничего не делаем */ },
            isReadOnly = true
        )
        binding.rvLearnedWords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLearnedWords.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWordsByStatus(true).collectLatest { words ->
                adapter.submitList(words)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 