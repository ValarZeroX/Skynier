package app.skynier.skynier.repository

import androidx.lifecycle.LiveData
import app.skynier.skynier.database.dao.RecordDao
import app.skynier.skynier.database.entities.RecordEntity

class RecordRepository(private val recordDao: RecordDao) {

    suspend fun insertRecord(record: RecordEntity) {
        recordDao.insertRecord(record)
    }

    suspend fun updateRecord(record: RecordEntity) {
        recordDao.updateRecord(record)
    }

    suspend fun deleteRecord(record: RecordEntity) {
        recordDao.deleteRecord(record)
    }

    // Ensure this returns LiveData<RecordEntity?>
    fun getRecordById(id: Int): LiveData<RecordEntity?> {
        return recordDao.getRecordById(id)
    }

    fun getAllRecords(): LiveData<List<RecordEntity>> {
        return recordDao.getAllRecords()
    }

    fun getRecordsByAccount(accountId: Int): LiveData<List<RecordEntity>> {
        return recordDao.getRecordsByAccount(accountId)
    }

    fun getRecordsByCategory(mainCategoryId: Int, subCategoryId: Int): LiveData<List<RecordEntity>> {
        return recordDao.getRecordsByCategory(mainCategoryId, subCategoryId)
    }

    fun getRecordsByDateRange(startDate: Long, endDate: Long): LiveData<List<RecordEntity>> {
        return recordDao.getRecordsByDateRange(startDate, endDate)
    }

    suspend fun deleteRecordById(recordId: Int) {
        recordDao.deleteRecordById(recordId) // 假设你的 DAO 中有这个方法
    }

    fun getRecordsByDateRangeAccountId(startDate: Long, endDate: Long, accountId: Int): LiveData<List<RecordEntity>> {
        return recordDao.getRecordsByDateRangeAccountId(startDate, endDate, accountId)
    }

    suspend fun deleteRecordsByAccountId(accountId: Int) {
        recordDao.deleteRecordsByAccountId(accountId)
    }
}