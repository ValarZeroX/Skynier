package app.skynier.skynier.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.MainCategoryEntity

@Dao
interface AccountDao {

    // 插入新的帳戶
    @Insert
    suspend fun insertAccount(account: AccountEntity)

    // 更新帳戶
    @Update
    suspend fun updateAccount(account: AccountEntity)

    // 刪除帳戶
    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    // 根據ID獲取帳戶
    @Query("SELECT * FROM account WHERE accountId = :accountId")
    suspend fun getAccountById(accountId: Int): AccountEntity?

    // 獲取所有帳戶
    @Query("SELECT * FROM account ORDER BY accountSort ASC")
    suspend fun getAllAccounts(): List<AccountEntity>

    // 根據幣別獲取帳戶
    @Query("SELECT * FROM account WHERE currency = :currencyCode")
    suspend fun getAccountsByCurrency(currencyCode: String): List<AccountEntity>

    @Update
    suspend fun updateAll(account: List<AccountEntity>)
}