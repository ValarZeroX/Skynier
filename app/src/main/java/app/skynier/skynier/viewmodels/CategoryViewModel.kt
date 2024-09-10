package app.skynier.skynier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.skynier.skynier.database.entities.CategoryEntity
import app.skynier.skynier.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryEntity>>()
    val categories: LiveData<List<CategoryEntity>> get() = _categories

    // 加載所有類別
    fun loadAllCategories() {
        viewModelScope.launch {
            val categoryList = repository.getAllCategories()
            _categories.value = categoryList
        }
    }

    // 根據ID加載單個類別
    fun loadCategoryById(id: Int) {
        viewModelScope.launch {
            val category = repository.getCategoryById(id)
            category?.let {
                _categories.value = listOf(it)
            }
        }
    }

    // 插入類別
    fun insertCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.insertCategory(category)
            loadAllCategories() // 插入後刷新數據
        }
    }

    // 更新類別
    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.updateCategory(category)
            loadAllCategories() // 更新後刷新數據
        }
    }

    // 刪除類別
    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            loadAllCategories() // 刪除後刷新數據
        }
    }
}

class CategoryViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}