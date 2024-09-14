package app.skynier.skynier.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import app.skynier.skynier.database.entities.AccountCategoryEntity
import app.skynier.skynier.database.entities.MainCategoryEntity

@Dao
interface AccountCategoryDao {

    // 插入新的帳戶分類
    @Insert
    suspend fun insertAccountCategory(accountCategory: AccountCategoryEntity)

    // 更新帳戶分類
    @Update
    suspend fun updateAccountCategory(accountCategory: AccountCategoryEntity)

    // 刪除帳戶分類
    @Delete
    suspend fun deleteAccountCategory(accountCategory: AccountCategoryEntity)

    // 根據ID獲取帳戶分類
    @Query("SELECT * FROM account_category WHERE accountCategoryId = :accountCategoryId")
    suspend fun getAccountCategoryById(accountCategoryId: Int): AccountCategoryEntity?

    // 獲取所有帳戶分類，按照排序順序
    @Query("SELECT * FROM account_category ORDER BY accountCategorySort ASC")
    suspend fun getAllAccountCategories(): List<AccountCategoryEntity>

    @Update
    suspend fun updateAll(accountCategory: List<AccountCategoryEntity>)
}