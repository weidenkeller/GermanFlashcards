package com.example.germanflashcards.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.germanflashcards.data.model.Word

class WordDiffCallback : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem == newItem
    }
}