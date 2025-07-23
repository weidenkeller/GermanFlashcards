package com.example.germanflashcards.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.germanflashcards.data.model.Word
import com.example.germanflashcards.databinding.ItemWordBinding

class WordAdapter(
    private val onMarkLearned: (Word) -> Unit,
    private val onEditWord: (Word) -> Unit,
    private val isReadOnly: Boolean = false
) : ListAdapter<Word, WordAdapter.WordViewHolder>(WordDiffCallback()) {
    
    class WordViewHolder(
        private val binding: ItemWordBinding,
        private val onMarkLearned: (Word) -> Unit,
        private val onEditWord: (Word) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.germanWord.text = word.german
            binding.russianWord.text = word.russian
            
            // Показываем изображение, если оно есть
            if (!word.imageUri.isNullOrEmpty()) {
                binding.wordImage.visibility = View.VISIBLE
                binding.wordImage.setImageURI(android.net.Uri.parse(word.imageUri))
            } else {
                binding.wordImage.visibility = View.GONE
            }
            
            // Начальное состояние - показываем немецкое слово
            binding.frontLayout.visibility = View.VISIBLE
            binding.backLayout.visibility = View.GONE

            binding.root.setOnClickListener {
                if (binding.frontLayout.visibility == View.VISIBLE) {
                    // Показываем русский перевод
                    binding.frontLayout.visibility = View.GONE
                    binding.backLayout.visibility = View.VISIBLE
                } else {
                    // Показываем немецкое слово
                    binding.frontLayout.visibility = View.VISIBLE
                    binding.backLayout.visibility = View.GONE
                }
            }

            // Обработка клика по кнопке "выучено"
            binding.btnMarkLearned.setOnClickListener {
                onMarkLearned(word)
            }
            // Обработка клика по кнопке "редактировать"
            binding.btnEditWord.setOnClickListener {
                onEditWord(word)
            }
            // Кнопки редактирования и выучено
            var isReadOnly: Boolean = false
            if (isReadOnly) {
                binding.btnEditWord.visibility = View.GONE
                binding.btnMarkLearned.visibility = View.GONE
                binding.tvLearned.visibility = View.GONE
            } else {
                binding.btnEditWord.visibility = View.VISIBLE
                binding.btnMarkLearned.visibility = View.VISIBLE
                // Визуальное выделение выученного слова и надписи
                if (word.isLearned) {
                    binding.tvLearned.visibility = View.VISIBLE
                } else {
                    binding.tvLearned.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding, onMarkLearned, onEditWord)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}