package com.example.germanflashcards.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.germanflashcards.databinding.FragmentCategoryWordsBinding
import com.example.germanflashcards.viewmodel.WordViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.germanflashcards.R
import com.example.germanflashcards.ui.adapter.WordAdapter
import com.example.germanflashcards.data.model.Word
import android.app.AlertDialog
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.example.germanflashcards.data.model.Category

class CategoryWordsFragment : Fragment() {
    private var _binding: FragmentCategoryWordsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private var categoryName: String = ""
    private var categoryId: Long = -1L
    private lateinit var wordAdapter: WordAdapter
    private var showLearned: Boolean = false
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryWordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Получаем название и id категории из аргументов
        arguments?.let { args ->
            categoryName = args.getString("categoryName", "")
            categoryId = args.getLong("categoryId", -1L)
        }
        
        setupUI()
        setupClickListeners()
        setupRecyclerView()
        setupToggleButtons()
        loadWords()
    }

    private fun setupUI() {
        binding.tvCategoryTitle.text = getString(R.string.category_with_name, categoryName)
    }

    private fun setupClickListeners() {
        // Кнопка "Назад"
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        // Удалены лишние обработчики
    }

    private fun setupRecyclerView() {
        wordAdapter = WordAdapter(
            onMarkLearned = { word ->
                val updatedWord = word.copy(isLearned = !word.isLearned)
                viewModel.updateWord(updatedWord)
            },
            onEditWord = { word ->
                showEditWordDialog(word)
            },
            isReadOnly = false
        )
        binding.recyclerWords.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wordAdapter
        }
    }

    private fun setupToggleButtons() {
        binding.btnShowUnlearned.setOnClickListener {
            showLearned = false
            updateToggleUI()
            loadWords()
        }
        binding.btnShowLearned.setOnClickListener {
            showLearned = true
            updateToggleUI()
            loadWords()
        }
    }

    private fun updateToggleUI() {
        binding.btnShowUnlearned.isEnabled = showLearned
        binding.btnShowLearned.isEnabled = !showLearned
    }

    private fun loadWords() {
        if (categoryId == -1L) return
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWordsByCategoryAndStatus(categoryId, showLearned).collectLatest { words ->
                wordAdapter.submitList(words)
            }
        }
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