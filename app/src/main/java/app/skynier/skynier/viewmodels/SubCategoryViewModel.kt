package app.skynier.skynier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.skynier.skynier.database.entities.SubCategoryEntity
import app.skynier.skynier.repository.SubCategoryRepository
import kotlinx.coroutines.launch

class SubCategoryViewModel(private val repository: SubCategoryRepository) : ViewModel() {

    private val _subCategories = MutableLiveData<List<SubCategoryEntity>>()
    val subCategories: LiveData<List<SubCategoryEntity>> get() = _subCategories

    // 加載所有子類別
    fun loadAllSubCategories() {
        viewModelScope.launch {
            val subCategoryList = repository.getAllSubCategories()
            _subCategories.value = subCategoryList
        }
    }

    // 根據主類別ID加載子類別
    fun loadSubCategoriesByMainCategoryId(mainCategoryId: Int) {
        viewModelScope.launch {
            val subCategoryList = repository.getSubCategoriesByMainCategory(mainCategoryId)
            _subCategories.value = subCategoryList
        }
    }

    // 根據ID加載單個子類別
    fun loadSubCategoryById(id: Int) {
        viewModelScope.launch {
            val subCategory = repository.getSubCategoryById(id)
            subCategory?.let {
                _subCategories.value = listOf(it)
            }
        }
    }

    // 插入子類別
    fun insertSubCategory(subCategory: SubCategoryEntity) {
        viewModelScope.launch {
            repository.insertSubCategory(subCategory)
            loadAllSubCategories() // 插入後刷新數據
        }
    }

    // 更新子類別
    fun updateSubCategory(subCategory: SubCategoryEntity) {
        viewModelScope.launch {
            repository.updateSubCategory(subCategory)
            loadAllSubCategories() // 更新後刷新數據
        }
    }

    // 刪除子類別
    fun deleteSubCategory(subCategory: SubCategoryEntity) {
        viewModelScope.launch {
            repository.deleteSubCategory(subCategory)
            loadAllSubCategories() // 刪除後刷新數據
        }
    }
}

class SubCategoryViewModelFactory(private val repository: SubCategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubCategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}