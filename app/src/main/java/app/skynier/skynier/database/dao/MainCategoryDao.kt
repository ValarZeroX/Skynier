package app.skynier.skynier.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.database.entities.MainCategoryEntity

@Dao
interface MainCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMainCategory(mainCategory: MainCategoryEntity)

    @Update
    suspend fun updateMainCategory(mainCategory: MainCategoryEntity)

    @Delete
    suspend fun deleteMainCategory(mainCategory: MainCategoryEntity)

    @Query("SELECT * FROM main_category WHERE mainCategoryId = :id")
    suspend fun getMainCategoryById(id: Int): MainCategoryEntity?

    @Query("SELECT * FROM main_category ORDER BY mainCategorySort")
    suspend fun getAllMainCategories(): List<MainCategoryEntity>

    @Query("SELECT * FROM main_category WHERE categoryId = :categoryId ORDER BY mainCategorySort")
    suspend fun getAllMainCategoriesByCategoryId(categoryId: Int): List<MainCategoryEntity>

    @Update
    suspend fun updateAll(mainCategory: List<MainCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMainCategories(mainCategory: List<MainCategoryEntity>)
}