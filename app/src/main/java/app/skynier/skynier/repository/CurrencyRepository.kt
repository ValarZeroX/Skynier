package app.skynier.skynier.repository

import app.skynier.skynier.database.dao.CurrencyDao
import app.skynier.skynier.database.entities.CurrencyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyRepository(private val currencyDao: CurrencyDao) {

    // 插入幣別
    suspend fun insertCurrency(currency: CurrencyEntity) {
        withContext(Dispatchers.IO) {
            currencyDao.insertCurrency(currency)
        }
    }

    // 更新幣別
    suspend fun updateCurrency(currency: CurrencyEntity) {
        withContext(Dispatchers.IO) {
            currencyDao.updateCurrency(currency)
        }
    }

    // 刪除幣別
    suspend fun deleteCurrency(currency: CurrencyEntity) {
        withContext(Dispatchers.IO) {
            currencyDao.deleteCurrency(currency)
        }
    }

    // 根據ID取得幣別
    suspend fun getCurrencyById(id: Int): CurrencyEntity? {
        return withContext(Dispatchers.IO) {
            currencyDao.getCurrencyById(id)
        }
    }

    // 取得所有幣別
    suspend fun getAllCurrencies(): List<CurrencyEntity> {
        return withContext(Dispatchers.IO) {
            currencyDao.getAllCurrencies()
        }
    }
}