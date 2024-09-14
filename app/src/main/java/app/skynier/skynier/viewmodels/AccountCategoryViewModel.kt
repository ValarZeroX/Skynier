package app.skynier.skynier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.skynier.skynier.database.entities.AccountCategoryEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.repository.AccountCategoryRepository
import kotlinx.coroutines.launch

class AccountCategoryViewModel(private val repository: AccountCategoryRepository) : ViewModel() {

    private val _accountCategories = MutableLiveData<List<AccountCategoryEntity>>()
    val accountCategories: LiveData<List<AccountCategoryEntity>> get() = _accountCategories

    // 加載所有帳戶分類
    fun loadAllAccountCategories() {
        viewModelScope.launch {
            val categoryList = repository.getAllAccountCategories()
            _accountCategories.value = categoryList
        }
    }

    // 根據ID加載單個帳戶分類
    fun loadAccountCategoryById(id: Int) {
        viewModelScope.launch {
            val category = repository.getAccountCategoryById(id)
            category?.let {
                _accountCategories.value = listOf(it)
            }
        }
    }

    // 插入帳戶分類
    fun insertAccountCategory(accountCategory: AccountCategoryEntity) {
        viewModelScope.launch {
            repository.insertAccountCategory(accountCategory)
            loadAllAccountCategories() // 插入後刷新數據
        }
    }

    // 更新帳戶分類
    fun updateAccountCategory(accountCategory: AccountCategoryEntity) {
        viewModelScope.launch {
            repository.updateAccountCategory(accountCategory)
            loadAllAccountCategories() // 更新後刷新數據
        }
    }

    // 刪除帳戶分類
    fun deleteAccountCategory(accountCategory: AccountCategoryEntity) {
        viewModelScope.launch {
            repository.deleteAccountCategory(accountCategory)
            loadAllAccountCategories() // 刪除後刷新數據
        }
    }

    fun updateAccountCategoryOrder(newOrder: List<AccountCategoryEntity>) = viewModelScope.launch {
        newOrder.forEachIndexed { index, accountCategory ->
            accountCategory.accountCategorySort = index
        }
        repository.updateAll(newOrder)
        _accountCategories.value = newOrder
    }
}

class AccountCategoryViewModelFactory(private val repository: AccountCategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountCategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}