package com.example.germanflashcards.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.germanflashcards.data.model.Word
import com.example.germanflashcards.databinding.ItemWordBinding

class WordAdapter(
    private val onMarkLearned: (Word) -> Unit
) : ListAdapter<Word, WordAdapter.WordViewHolder>(WordDiffCallback()) {
    
    class WordViewHolder(private val binding: ItemWordBinding, private val onMarkLearned: (Word) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.russianWord.text = word.russian
            binding.germanWord.text = word.german
            
            // Показываем изображение, если оно есть
            if (!word.imageUri.isNullOrEmpty()) {
                binding.wordImage.visibility = View.VISIBLE
                binding.wordImage.setImageURI(android.net.Uri.parse(word.imageUri))
            } else {
                binding.wordImage.visibility = View.GONE
            }
            
            // Начальное состояние - показываем русское слово
            binding.frontLayout.visibility = View.VISIBLE
            binding.backLayout.visibility = View.GONE

            binding.root.setOnClickListener {
                if (binding.frontLayout.visibility == View.VISIBLE) {
                    // Показываем немецкое слово
                    binding.frontLayout.visibility = View.GONE
                    binding.backLayout.visibility = View.VISIBLE
                } else {
                    // Показываем русское слово
                    binding.frontLayout.visibility = View.VISIBLE
                    binding.backLayout.visibility = View.GONE
                }
            }

            // Обработка клика по кнопке "выучено"
            binding.btnMarkLearned.setOnClickListener {
                onMarkLearned(word)
            }
            // Визуальное выделение выученного слова и надписи
            if (word.isLearned) {
                binding.btnMarkLearned.setColorFilter(
                    binding.root.context.getColor(com.example.germanflashcards.R.color.green)
                )
                binding.btnMarkLearned.alpha = 1.0f
                binding.tvLearned.visibility = View.VISIBLE
                binding.root.setCardBackgroundColor(
                    binding.root.context.getColor(com.example.germanflashcards.R.color.white)
                )
            } else {
                binding.btnMarkLearned.setColorFilter(
                    binding.root.context.getColor(android.R.color.darker_gray)
                )
                binding.btnMarkLearned.alpha = 0.5f
                binding.tvLearned.visibility = View.GONE
                binding.root.setCardBackgroundColor(
                    binding.root.context.getColor(android.R.color.background_light)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding, onMarkLearned)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}