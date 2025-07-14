package com.example.germanflashcards.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.germanflashcards.data.model.Word
import com.example.germanflashcards.databinding.ItemWordBinding

class WordAdapter : ListAdapter<Word, WordAdapter.WordViewHolder>(WordDiffCallback()) {
    
    class WordViewHolder(private val binding: ItemWordBinding) : RecyclerView.ViewHolder(binding.root) {
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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}