package app.skynier.skynier.repository

import app.skynier.skynier.database.dao.CategoryDao
import app.skynier.skynier.database.entities.CategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(private val categoryDao: CategoryDao) {

    // 插入類別
    suspend fun insertCategory(category: CategoryEntity) {
        withContext(Dispatchers.IO) {
            categoryDao.insertCategory(category)
        }
    }

    // 更新類別
    suspend fun updateCategory(category: CategoryEntity) {
        withContext(Dispatchers.IO) {
            categoryDao.updateCategory(category)
        }
    }

    // 刪除類別
    suspend fun deleteCategory(category: CategoryEntity) {
        withContext(Dispatchers.IO) {
            categoryDao.deleteCategory(category)
        }
    }

    // 取得特定ID的類別
    suspend fun getCategoryById(id: Int): CategoryEntity? {
        return withContext(Dispatchers.IO) {
            categoryDao.getCategoryById(id)
        }
    }

    // 取得所有類別
    suspend fun getAllCategories(): List<CategoryEntity> {
        return withContext(Dispatchers.IO) {
            categoryDao.getAllCategories()
        }
    }
}