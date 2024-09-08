package app.skynier.skynier.repository

import app.skynier.skynier.database.dao.SubCategoryDao
import app.skynier.skynier.database.entities.SubCategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubCategoryRepository(private val subCategoryDao: SubCategoryDao) {

    // 插入新的子類別
    suspend fun insertSubCategory(subCategory: SubCategoryEntity) {
        withContext(Dispatchers.IO) {
            subCategoryDao.insertSubCategory(subCategory)
        }
    }

    // 更新子類別
    suspend fun updateSubCategory(subCategory: SubCategoryEntity) {
        withContext(Dispatchers.IO) {
            subCategoryDao.updateSubCategory(subCategory)
        }
    }

    // 刪除子類別
    suspend fun deleteSubCategory(subCategory: SubCategoryEntity) {
        withContext(Dispatchers.IO) {
            subCategoryDao.deleteSubCategory(subCategory)
        }
    }

    // 根據 ID 獲取子類別
    suspend fun getSubCategoryById(id: Int): SubCategoryEntity? {
        return withContext(Dispatchers.IO) {
            subCategoryDao.getSubCategoryById(id)
        }
    }

    // 獲取所有的子類別
    suspend fun getAllSubCategories(): List<SubCategoryEntity> {
        return withContext(Dispatchers.IO) {
            subCategoryDao.getAllSubCategories()
        }
    }

    // 根據主類別 ID 獲取所有子類別
    suspend fun getSubCategoriesByMainCategory(mainCategoryId: Int): List<SubCategoryEntity> {
        return withContext(Dispatchers.IO) {
            subCategoryDao.getSubCategoriesByMainCategoryId(mainCategoryId)
        }
    }
}