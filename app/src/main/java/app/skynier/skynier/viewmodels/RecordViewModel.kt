package app.skynier.skynier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.repository.RecordRepository
import kotlinx.coroutines.launch

class RecordViewModel(private val repository: RecordRepository) : ViewModel() {

    fun insertRecord(record: RecordEntity) = viewModelScope.launch {
        repository.insertRecord(record)
    }

    fun updateRecord(record: RecordEntity) = viewModelScope.launch {
        repository.updateRecord(record)
    }

    fun deleteRecord(record: RecordEntity) = viewModelScope.launch {
        repository.deleteRecord(record)
    }

    fun getRecordById(id: Int): LiveData<RecordEntity?> {
        return repository.getRecordById(id)
    }

    fun getAllRecords(): LiveData<List<RecordEntity>> {
        return repository.getAllRecords()
    }

    fun getRecordsByAccount(accountId: Int): LiveData<List<RecordEntity>> {
        return repository.getRecordsByAccount(accountId)
    }

    fun getRecordsByCategory(mainCategoryId: Int, subCategoryId: Int): LiveData<List<RecordEntity>> {
        return repository.getRecordsByCategory(mainCategoryId, subCategoryId)
    }
}

class RecordViewModelFactory(private val repository: RecordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}