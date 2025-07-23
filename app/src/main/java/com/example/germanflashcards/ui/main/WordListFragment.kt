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
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import android.app.AlertDialog
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.net.Uri
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import com.example.germanflashcards.data.model.Category
import android.text.Editable
import android.text.TextWatcher

class WordListFragment : Fragment() {
    private var _binding: FragmentWordListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private lateinit var wordAdapter: WordAdapter
    private var selectedImageUri: Uri? = null
    private var selectedCategoryId: Long? = null
    private var editImageView: ImageView? = null
    private var editWord: com.example.germanflashcards.data.model.Word? = null
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            editImageView?.setImageURI(it)
            editImageView?.visibility = View.VISIBLE
        }
    }
    private var allWords: List<com.example.germanflashcards.data.model.Word> = emptyList()

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
        setupLanguageSwitcher()
        setupSearch()
    }
    
    private fun setupRecyclerView() {
        wordAdapter = WordAdapter(
            onMarkLearned = { word ->
                val updatedWord = word.copy(isLearned = !word.isLearned)
                viewModel.updateWord(updatedWord)
            },
            onEditWord = { word ->
                showEditWordDialog(word)
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wordAdapter
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWordsByCategory(1).collectLatest { words ->
                allWords = words
                wordAdapter.submitList(words)
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnAddWord.setOnClickListener {
            findNavController().navigate(R.id.action_to_addWord)
        }
    }

    private val languages = listOf("ru", "de", "en")
    private fun setupLanguageSwitcher() {
        val currentLang = getCurrentLang()
        updateLangSwitcher(currentLang)
        binding.langSwitcherBlock.setOnClickListener {
            val nextLang = getNextLang(getCurrentLang())
            setLocale(nextLang)
            updateLangSwitcher(nextLang)
        }
    }
    private fun updateLangSwitcher(lang: String) {
        val label = when(lang) {
            "ru" -> "RU"
            "de" -> "DE"
            "en" -> "EN"
            else -> lang.uppercase()
        }
        binding.tvLangSwitcher.text = label
    }
    private fun getCurrentLang(): String {
        return Locale.getDefault().language
    }
    private fun getNextLang(current: String): String {
        val idx = languages.indexOf(current)
        return languages[(idx + 1) % languages.size]
    }
    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)
        requireActivity().recreate()
    }

    private fun setupSearch() {
        binding.btnShowSearch.setOnClickListener {
            binding.etSearch.visibility =
                if (binding.etSearch.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            if (binding.etSearch.visibility == View.VISIBLE) {
                binding.etSearch.requestFocus()
            } else {
                binding.etSearch.setText("")
                wordAdapter.submitList(allWords)
            }
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim()?.lowercase() ?: ""
                if (query.isEmpty()) {
                    wordAdapter.submitList(allWords)
                } else {
                    val filtered = allWords.filter {
                        it.russian.lowercase().contains(query) ||
                        it.german.lowercase().contains(query) ||
                        (it.imageUri?.lowercase()?.contains(query) ?: false)
                    }
                    wordAdapter.submitList(filtered)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showEditWordDialog(word: com.example.germanflashcards.data.model.Word) {
        editWord = word
        val context = requireContext()
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_word, null)
        val etRussian = dialogView.findViewById<EditText>(R.id.etEditRussianWord)
        val etGerman = dialogView.findViewById<EditText>(R.id.etEditGermanWord)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerEditCategory)
        val imageView = dialogView.findViewById<ImageView>(R.id.ivEditWordImage)
        val btnSelectImage = dialogView.findViewById<View>(R.id.btnEditSelectImage)
        editImageView = imageView
        etRussian.setText(word.russian)
        etGerman.setText(word.german)
        if (!word.imageUri.isNullOrEmpty()) {
            imageView.setImageURI(Uri.parse(word.imageUri))
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
        }
        btnSelectImage.setOnClickListener {
            getImage.launch("image/*")
        }
        // Категории
        val categories = viewModel.getCategoriesSync()
        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        val selectedIdx = categories.indexOfFirst { it.id == word.categoryId }
        if (selectedIdx >= 0) spinnerCategory.setSelection(selectedIdx)
        selectedCategoryId = word.categoryId
        spinnerCategory.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategoryId = categories[position].id
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })
        AlertDialog.Builder(context)
            .setTitle(R.string.edit)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val updatedWord = word.copy(
                    russian = etRussian.text.toString(),
                    german = etGerman.text.toString(),
                    imageUri = selectedImageUri?.toString() ?: word.imageUri,
                    categoryId = selectedCategoryId ?: word.categoryId
                )
                viewModel.updateWord(updatedWord)
            }
            .setNegativeButton(R.string.cancel, null)
            .setNeutralButton(R.string.delete) { _, _ ->
                viewModel.deleteWord(word)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}