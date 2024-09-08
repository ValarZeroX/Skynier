package app.skynier.skynier.repository

import app.skynier.skynier.database.dao.MainCategoryDao
import app.skynier.skynier.database.entities.MainCategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainCategoryRepository(private val mainCategoryDao: MainCategoryDao) {

    // 插入一条主类别记录
    suspend fun insertMainCategory(mainCategory: MainCategoryEntity) {
        withContext(Dispatchers.IO) {
            mainCategoryDao.insertMainCategory(mainCategory)
        }
    }

    // 更新一条主类别记录
    suspend fun updateMainCategory(mainCategory: MainCategoryEntity) {
        withContext(Dispatchers.IO) {
            mainCategoryDao.updateMainCategory(mainCategory)
        }
    }

    // 删除一条主类别记录
    suspend fun deleteMainCategory(mainCategory: MainCategoryEntity) {
        withContext(Dispatchers.IO) {
            mainCategoryDao.deleteMainCategory(mainCategory)
        }
    }

    // 通过ID获取主类别记录
    suspend fun getMainCategoryById(id: Int): MainCategoryEntity? {
        return withContext(Dispatchers.IO) {
            mainCategoryDao.getMainCategoryById(id)
        }
    }

    // 获取所有主类别记录
    suspend fun getAllMainCategories(): List<MainCategoryEntity> {
        return withContext(Dispatchers.IO) {
            mainCategoryDao.getAllMainCategories()
        }
    }
}