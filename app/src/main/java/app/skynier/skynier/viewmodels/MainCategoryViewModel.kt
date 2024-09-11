package app.skynier.skynier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.repository.MainCategoryRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class MainCategoryViewModel(private val repository: MainCategoryRepository) : ViewModel() {

    // LiveData 用于暴露给 UI 层观察
    private val _mainCategories = MutableLiveData<List<MainCategoryEntity>>()
    val mainCategories: LiveData<List<MainCategoryEntity>> get() = _mainCategories

    // 加载所有主类目
    fun loadAllMainCategories() {
        viewModelScope.launch {
            val categories = repository.getAllMainCategories()
            _mainCategories.value = categories
        }
    }

    // 根据 ID 加载单个主类目
    fun loadMainCategoryById(id: Int) {
        viewModelScope.launch {
            val category = repository.getMainCategoryById(id)
            category?.let {
                _mainCategories.value = listOf(it)
            }
        }
    }

    fun loadMainCategoriesByMainCategoryId(categoryId: Int) {
        viewModelScope.launch {
            val mainCategoryList = repository.getAllMainCategoriesByCategoryId(categoryId)
            _mainCategories.value = mainCategoryList
        }
    }

    // 插入新主类目
    fun insertMainCategory(mainCategory: MainCategoryEntity) {
        viewModelScope.launch {
            repository.insertMainCategory(mainCategory)
            loadMainCategoriesByMainCategoryId(mainCategory.categoryId) // 插入后刷新数据
        }
    }

    // 更新主类目
    fun updateMainCategory(mainCategory: MainCategoryEntity) {
        viewModelScope.launch {
            repository.updateMainCategory(mainCategory)
            loadMainCategoriesByMainCategoryId(mainCategory.categoryId) // 更新后刷新数据
        }
    }

    // 删除主类目
    fun deleteMainCategory(mainCategory: MainCategoryEntity) {
        viewModelScope.launch {
            repository.deleteMainCategory(mainCategory)
            loadMainCategoriesByMainCategoryId(mainCategory.categoryId) // 删除后刷新数据
        }
    }

    fun updateMainCategoryOrder(newOrder: List<MainCategoryEntity>) = viewModelScope.launch {
        newOrder.forEachIndexed { index, mainCategory ->
            mainCategory.mainCategorySort = index
        }
        repository.updateAll(newOrder)
        _mainCategories.value = newOrder
    }
}

class MainCategoryViewModelFactory(private val repository: MainCategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainCategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}