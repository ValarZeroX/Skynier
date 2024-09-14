package app.skynier.skynier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.repository.AccountRepository
import kotlinx.coroutines.launch

class AccountViewModel(private val repository: AccountRepository) : ViewModel() {

    private val _accounts = MutableLiveData<List<AccountEntity>>()
    val accounts: LiveData<List<AccountEntity>> get() = _accounts

    // 加載所有帳戶
    fun loadAllAccounts() {
        viewModelScope.launch {
            val accountList = repository.getAllAccounts()
            _accounts.value = accountList
        }
    }

    // 根據ID加載帳戶
    fun loadAccountById(id: Int) {
        viewModelScope.launch {
            val account = repository.getAccountById(id)
            account?.let {
                _accounts.value = listOf(it)
            }
        }
    }

    // 插入帳戶
    fun insertAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.insertAccount(account)
            loadAllAccounts() // 插入後刷新數據
        }
    }

    // 更新帳戶
    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.updateAccount(account)
            loadAllAccounts() // 更新後刷新數據
        }
    }

    // 刪除帳戶
    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.deleteAccount(account)
            loadAllAccounts() // 刪除後刷新數據
        }
    }
}

class AccountViewModelFactory(private val repository: AccountRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}