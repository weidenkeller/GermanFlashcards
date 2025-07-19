package com.example.germanflashcards.ui.add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.germanflashcards.R
import com.example.germanflashcards.data.model.Word
import com.example.germanflashcards.databinding.ActivityAddWordBinding
import com.example.germanflashcards.viewmodel.WordViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.example.germanflashcards.data.model.Category
import com.example.germanflashcards.data.AppDatabase
import kotlinx.coroutines.launch

class AddWordActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddWordBinding
    private lateinit var viewModel: WordViewModel
    private var selectedImageUri: Uri? = null
    private var selectedCategoryId: Long = 1 // По умолчанию первая категория
    private var categoryList: List<Category> = emptyList()
    
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { 
            selectedImageUri = it
            binding.imageView.setImageURI(it)
            binding.imageView.visibility = android.view.View.VISIBLE
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        loadCategories() // Загружаем категории
        setupUI()
        setupListeners()
    }

    private fun loadCategories() {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            categoryList = db.categoryDao().getAllCategoriesSync()
            val categoryNames = categoryList.map { it.name }
            val adapter = ArrayAdapter(this@AddWordActivity, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
            // По умолчанию выбираем первую категорию, если есть
            if (categoryList.isNotEmpty()) {
                selectedCategoryId = categoryList[0].id
            }
        }
        binding.spinnerCategory.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                if (categoryList.isNotEmpty()) {
                    selectedCategoryId = categoryList[position].id
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })
    }
    
    private fun setupUI() {
        // Настройка кнопки выбора изображения
        binding.btnSelectImage.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }
        
        // Настройка кнопки сохранения
        binding.btnSave.setOnClickListener {
            saveWord()
        }
    }
    
    private fun setupListeners() {
        // Здесь можно добавить логику для выбора категории
        // Пока используем первую категорию по умолчанию
    }
    
    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ использует READ_MEDIA_IMAGES
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 и ниже использует READ_EXTERNAL_STORAGE
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    private fun requestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            PERMISSION_REQUEST_CODE
        )
    }
    
    private fun openGallery() {
        getContent.launch("image/*")
    }
    
    private fun saveWord() {
        val russianWord = binding.etRussianWord.text.toString().trim()
        val germanWord = binding.etGermanWord.text.toString().trim()
        
        if (russianWord.isEmpty() || germanWord.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Сохраняем изображение в локальное хранилище приложения
        val savedImageUri = selectedImageUri?.let { saveImageToInternalStorage(it) }
        
        val word = Word(
            russian = russianWord,
            german = germanWord,
            imageUri = savedImageUri?.toString(),
            categoryId = selectedCategoryId
        )
        
        viewModel.addWord(word)
        Toast.makeText(this, "Слово добавлено!", Toast.LENGTH_SHORT).show()
        finish()
    }
    
    private fun saveImageToInternalStorage(uri: Uri): Uri? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(filesDir, "word_images/${System.currentTimeMillis()}.jpg")
            file.parentFile?.mkdirs()
            
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Разрешение необходимо для выбора изображения", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}

