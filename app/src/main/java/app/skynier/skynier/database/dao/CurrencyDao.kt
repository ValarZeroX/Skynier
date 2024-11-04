package app.skynier.skynier.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import app.skynier.skynier.database.entities.CategoryEntity
import app.skynier.skynier.database.entities.CurrencyEntity

@Dao
interface CurrencyDao {

    // 插入新的幣別
    @Insert
    suspend fun insertCurrency(currency: CurrencyEntity)

    // 更新幣別
    @Update
    suspend fun updateCurrency(currency: CurrencyEntity)

    // 刪除幣別
    @Delete
    suspend fun deleteCurrency(currency: CurrencyEntity)

    // 根據ID獲取幣別
    @Query("SELECT * FROM currency WHERE currencyId = :currencyId")
    suspend fun getCurrencyById(currencyId: Int): CurrencyEntity?

    // 獲取所有幣別
    @Query("SELECT * FROM currency")
    suspend fun getAllCurrencies(): List<CurrencyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currency: List<CurrencyEntity>)
}