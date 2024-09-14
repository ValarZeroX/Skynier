package app.skynier.skynier.repository

import app.skynier.skynier.database.dao.AccountCategoryDao
import app.skynier.skynier.database.entities.AccountCategoryEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountCategoryRepository(private val accountCategoryDao: AccountCategoryDao) {

    // 插入帳戶分類
    suspend fun insertAccountCategory(accountCategory: AccountCategoryEntity) {
        withContext(Dispatchers.IO) {
            accountCategoryDao.insertAccountCategory(accountCategory)
        }
    }

    // 更新帳戶分類
    suspend fun updateAccountCategory(accountCategory: AccountCategoryEntity) {
        withContext(Dispatchers.IO) {
            accountCategoryDao.updateAccountCategory(accountCategory)
        }
    }

    // 刪除帳戶分類
    suspend fun deleteAccountCategory(accountCategory: AccountCategoryEntity) {
        withContext(Dispatchers.IO) {
            accountCategoryDao.deleteAccountCategory(accountCategory)
        }
    }

    // 根據ID取得帳戶分類
    suspend fun getAccountCategoryById(id: Int): AccountCategoryEntity? {
        return withContext(Dispatchers.IO) {
            accountCategoryDao.getAccountCategoryById(id)
        }
    }

    // 取得所有帳戶分類
    suspend fun getAllAccountCategories(): List<AccountCategoryEntity> {
        return withContext(Dispatchers.IO) {
            accountCategoryDao.getAllAccountCategories()
        }
    }

    suspend fun updateAll(accountCategory: List<AccountCategoryEntity>) {
        accountCategoryDao.updateAll(accountCategory)
    }
}