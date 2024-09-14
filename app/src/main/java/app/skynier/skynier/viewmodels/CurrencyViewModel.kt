package app.skynier.skynier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.repository.CurrencyRepository
import kotlinx.coroutines.launch

class CurrencyViewModel(private val repository: CurrencyRepository) : ViewModel() {

    private val _currencies = MutableLiveData<List<CurrencyEntity>>()
    val currencies: LiveData<List<CurrencyEntity>> get() = _currencies

    // 加載所有幣別
    fun loadAllCurrencies() {
        viewModelScope.launch {
            val currencyList = repository.getAllCurrencies()
            _currencies.value = currencyList
        }
    }

    // 根據ID加載幣別
    fun loadCurrencyById(id: Int) {
        viewModelScope.launch {
            val currency = repository.getCurrencyById(id)
            currency?.let {
                _currencies.value = listOf(it)
            }
        }
    }

    // 插入幣別
    fun insertCurrency(currency: CurrencyEntity) {
        viewModelScope.launch {
            repository.insertCurrency(currency)
            loadAllCurrencies() // 插入後刷新數據
        }
    }

    // 更新幣別
    fun updateCurrency(currency: CurrencyEntity) {
        viewModelScope.launch {
            repository.updateCurrency(currency)
            loadAllCurrencies() // 更新後刷新數據
        }
    }

    // 刪除幣別
    fun deleteCurrency(currency: CurrencyEntity) {
        viewModelScope.launch {
            repository.deleteCurrency(currency)
            loadAllCurrencies() // 刪除後刷新數據
        }
    }
}

class CurrencyViewModelFactory(private val repository: CurrencyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrencyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}