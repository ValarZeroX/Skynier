package app.skynier.skynier.repository

import app.skynier.skynier.database.dao.AccountDao
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountRepository(private val accountDao: AccountDao) {

    // 插入帳戶
    suspend fun insertAccount(account: AccountEntity) {
        withContext(Dispatchers.IO) {
            accountDao.insertAccount(account)
        }
    }

    // 更新帳戶
    suspend fun updateAccount(account: AccountEntity) {
        withContext(Dispatchers.IO) {
            accountDao.updateAccount(account)
        }
    }

    // 刪除帳戶
    suspend fun deleteAccount(account: AccountEntity) {
        withContext(Dispatchers.IO) {
            accountDao.deleteAccount(account)
        }
    }

    // 根據ID取得帳戶
    suspend fun getAccountById(id: Int): AccountEntity? {
        return withContext(Dispatchers.IO) {
            accountDao.getAccountById(id)
        }
    }

    // 取得所有帳戶
    suspend fun getAllAccounts(): List<AccountEntity> {
        return withContext(Dispatchers.IO) {
            accountDao.getAllAccounts()
        }
    }

    suspend fun updateAll(account: List<AccountEntity>) {
        accountDao.updateAll(account)
    }
}