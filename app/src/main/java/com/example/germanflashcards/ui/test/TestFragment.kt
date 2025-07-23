package com.example.germanflashcards.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.germanflashcards.databinding.FragmentTestBinding
import android.app.AlertDialog
import android.widget.RadioGroup
import android.widget.Button
import android.widget.Toast
import com.example.germanflashcards.R
import androidx.navigation.fragment.findNavController

class TestFragment : Fragment() {
    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardCreateTest.setOnClickListener {
            showTestSettingsDialog()
        }
    }

    private fun showTestSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_test_settings, null)
        val rgWordCount = dialogView.findViewById<RadioGroup>(R.id.rgWordCount)
        val rgRepeat = dialogView.findViewById<RadioGroup>(R.id.rgRepeat)
        val btnStartTest = dialogView.findViewById<Button>(R.id.btnStartTest)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        btnStartTest.setOnClickListener {
            val wordCount = when (rgWordCount.checkedRadioButtonId) {
                R.id.rb5 -> 5
                R.id.rb10 -> 10
                R.id.rb20 -> 20
                else -> 5
            }
            val repeat = when (rgRepeat.checkedRadioButtonId) {
                R.id.rb3 -> 3
                R.id.rb4 -> 4
                R.id.rb5repeat -> 5
                else -> 3
            }
            dialog.dismiss()
            // Переход на TestSessionFragment с параметрами
            val bundle = Bundle().apply {
                putInt("wordCount", wordCount)
                putInt("repeatCount", repeat)
            }
            findNavController().navigate(R.id.testSessionFragment, bundle)
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 