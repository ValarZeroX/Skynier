package app.skynier.skynier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.repository.RecordRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

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

    fun getRecordsByDateRange(startDate: Long, endDate: Long): LiveData<List<RecordEntity>> {
        return repository.getRecordsByDateRange(startDate, endDate)
    }

    fun deleteRecordById(recordId: Int) = viewModelScope.launch {
        repository.deleteRecordById(recordId)
    }

    fun getDateSerialNumberMapByDateRange(
        startDate: Long,
        endDate: Long,
        searchText: String
    ): LiveData<Map<Int, Int>> {
        val result = MediatorLiveData<Map<Int, Int>>()

        val recordsLiveData = repository.getRecordsByDateRange(startDate, endDate)

        result.addSource(recordsLiveData) { records ->
            val filteredRecords = records.filter {
                it.name.contains(searchText, ignoreCase = true) ||
                        it.description.contains(searchText, ignoreCase = true)
            }
            val distinctDates = filteredRecords.map { record ->
                // 提取日期中的日（例如23日）
                record.datetime.let {
                    Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .dayOfMonth
                }
            }.distinct() // 去除重复的日期

            val dateSerialNumberMap = distinctDates.mapIndexed { index, day ->
                // 生成键值对：键为流水号，值为日期中的日
                index + 1 to day
            }.toMap()

            result.value = dateSerialNumberMap
        }

        return result
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