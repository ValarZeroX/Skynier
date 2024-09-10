package app.skynier.skynier.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.skynier.skynier.database.dao.CategoryDao
import app.skynier.skynier.database.dao.MainCategoryDao
import app.skynier.skynier.database.dao.SubCategoryDao
import app.skynier.skynier.database.entities.CategoryEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.SubCategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [CategoryEntity::class, MainCategoryEntity::class, SubCategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun mainCategoryDao(): MainCategoryDao
    abstract fun subCategoryDao(): SubCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
//            deleteDatabase(context)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
//                    .createFromAsset("database/stock.db")
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance

                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.mainCategoryDao(), database.categoryDao())
                    }
                }
            }

            suspend fun populateDatabase(mainCategoryDao: MainCategoryDao, categoryDao: CategoryDao) {
                if (categoryDao.getAllCategories().isEmpty()) {
                    val defaultCategory = listOf(
                        CategoryEntity(
                            categoryId = 1,
                            categoryIdNameKey = "expense",
                        ),
                        CategoryEntity(
                            categoryId = 2,
                            categoryIdNameKey = "income",
                        ),
                        CategoryEntity(
                            categoryId = 3,
                            categoryIdNameKey = "transfer",
                        )
                    )
                    defaultCategory.forEach { categoryDao.insertCategory(it) }
                }
                if (mainCategoryDao.getAllMainCategories().isEmpty()) {
                    val defaultMainCategory = listOf(
                        MainCategoryEntity(
                            mainCategoryId = 1,
                            categoryId = 1,
                            mainCategoryNameKey = "category_food",
                            mainCategoryIcon = "Restaurant",
                            mainCategoryBackgroundColor = "EACE13",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 0,
                        ),
                        MainCategoryEntity(
                            mainCategoryId = 2,
                            categoryId = 1,
                            mainCategoryNameKey = "category_transportation",
                            mainCategoryIcon = "Commute",
                            mainCategoryBackgroundColor = "23B0F0",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 1,
                        ),
                        MainCategoryEntity(
                            mainCategoryId = 3,
                            categoryId = 1,
                            mainCategoryNameKey = "category_travel",
                            mainCategoryIcon = "FlightTakeoff",
                            mainCategoryBackgroundColor = "4C4414",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 2,
                        ),
                        MainCategoryEntity(
                            mainCategoryId = 4,
                            categoryId = 1,
                            mainCategoryNameKey = "category_social",
                            mainCategoryIcon = "Forum",
                            mainCategoryBackgroundColor = "7F7F84",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 3,
                        ),
                        MainCategoryEntity(
                            mainCategoryId = 5,
                            categoryId = 1,
                            mainCategoryNameKey = "category_social",
                            mainCategoryIcon = "Group",
                            mainCategoryBackgroundColor = "D1CEBE",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 4,
                        ),
                        MainCategoryEntity(
                            mainCategoryId = 6,
                            categoryId = 1,
                            mainCategoryNameKey = "category_social",
                            mainCategoryIcon = "Group",
                            mainCategoryBackgroundColor = "444C4C",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 5,
                        ),
                    )
                    defaultMainCategory.forEach { mainCategoryDao.insertMainCategory(it) }
                }
            }
        }

        private fun deleteDatabase(context: Context) {
            context.deleteDatabase("app_database")
        }
    }
}