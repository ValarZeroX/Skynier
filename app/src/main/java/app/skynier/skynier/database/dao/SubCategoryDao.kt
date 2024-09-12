package app.skynier.skynier.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.SubCategoryEntity

@Dao
interface SubCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubCategory(subCategory: SubCategoryEntity)

    @Update
    suspend fun updateSubCategory(subCategory: SubCategoryEntity)

    @Delete
    suspend fun deleteSubCategory(subCategory: SubCategoryEntity)

    @Query("SELECT * FROM sub_category WHERE subCategoryId = :id")
    suspend fun getSubCategoryById(id: Int): SubCategoryEntity?

    @Query("SELECT * FROM sub_category WHERE mainCategoryId = :mainCategoryId ORDER BY subCategorySort")
    suspend fun getSubCategoriesByMainCategoryId(mainCategoryId: Int): List<SubCategoryEntity>

    @Query("SELECT * FROM sub_category")
    suspend fun getAllSubCategories(): List<SubCategoryEntity>

    @Update
    suspend fun updateAll(subCategory: List<SubCategoryEntity>)
}