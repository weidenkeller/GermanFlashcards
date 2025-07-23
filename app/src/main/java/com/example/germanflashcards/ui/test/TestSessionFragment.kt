package com.example.germanflashcards.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.germanflashcards.databinding.FragmentTestSessionBinding
import com.example.germanflashcards.viewmodel.WordViewModel
import kotlinx.coroutines.flow.first
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.germanflashcards.data.model.Word
import com.example.germanflashcards.R
import androidx.navigation.fragment.findNavController

class TestSessionFragment : Fragment() {
    private var _binding: FragmentTestSessionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()

    private var testWords: List<Word> = emptyList()
    private var currentIndex = 0
    private var total = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Получаем параметры теста из аргументов
        total = arguments?.getInt("wordCount") ?: 5
        lifecycleScope.launch {
            val allLearned = viewModel.getWordsByStatus(true).first()
            testWords = allLearned.shuffled().take(total)
            if (testWords.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.learned_words_count, 0), Toast.LENGTH_LONG).show()
                requireActivity().onBackPressed()
                return@launch
            }
            currentIndex = 0
            showCurrentWord()
        }
        binding.btnCheckAnswer.setOnClickListener {
            checkAnswer()
        }
    }

    private fun showCurrentWord() {
        if (currentIndex >= testWords.size) {
            Toast.makeText(requireContext(), "Тест завершён!", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }
        val word = testWords[currentIndex]
        binding.tvGermanWord.text = word.german
        binding.etUserAnswer.setText("")
    }

    private fun checkAnswer() {
        val word = testWords[currentIndex]
        val userAnswer = binding.etUserAnswer.text.toString().trim().lowercase()
        val correctAnswer = word.russian.trim().lowercase()
        if (userAnswer == correctAnswer) {
            showCustomToast(true)
        } else {
            showCustomToast(false)
        }
        currentIndex++
        showCurrentWord()
    }

    private fun showCustomToast(isCorrect: Boolean) {
        val layoutId = if (isCorrect) R.layout.toast_correct else R.layout.toast_incorrect
        val layout = LayoutInflater.from(requireContext()).inflate(layoutId, null)
        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 