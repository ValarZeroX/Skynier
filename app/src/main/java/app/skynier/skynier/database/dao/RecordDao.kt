package app.skynier.skynier.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.skynier.skynier.database.entities.RecordEntity

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity)

    @Update
    suspend fun updateRecord(record: RecordEntity)

    @Delete
    suspend fun deleteRecord(record: RecordEntity)

    // Change this method to return LiveData
    @Query("SELECT * FROM record WHERE recordId = :id")
    fun getRecordById(id: Int): LiveData<RecordEntity?>

    @Query("SELECT * FROM record ORDER BY datetime DESC")
    fun getAllRecords(): LiveData<List<RecordEntity>>

    @Query("SELECT * FROM record WHERE accountId = :accountId ORDER BY datetime DESC")
    fun getRecordsByAccount(accountId: Int): LiveData<List<RecordEntity>>

    @Query("SELECT * FROM record WHERE mainCategoryId = :mainCategoryId AND subCategoryId = :subCategoryId ORDER BY datetime DESC")
    fun getRecordsByCategory(mainCategoryId: Int, subCategoryId: Int): LiveData<List<RecordEntity>>
}