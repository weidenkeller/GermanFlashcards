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
    }
    
    private fun setupRecyclerView() {
        wordAdapter = WordAdapter()
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
        binding.fabAddWord.setOnClickListener {
            findNavController().navigate(R.id.action_to_addWord)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}