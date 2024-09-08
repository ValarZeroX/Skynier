package app.skynier.skynier.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.skynier.skynier.database.dao.MainCategoryDao
import app.skynier.skynier.database.dao.SubCategoryDao
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.SubCategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [MainCategoryEntity::class, SubCategoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
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
                    "app_database")
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
                        populateDatabase(database.mainCategoryDao())
                    }
                }
            }

            suspend fun populateDatabase(mainCategoryDao: MainCategoryDao) {
                // 插入预设值

                val defaultStockMarkets = listOf(
                    MainCategoryEntity(mainCategoryId = 1 ,mainCategoryNameKey = "category_food", mainCategoryIcon = "台股", mainCategoryBackgroundColor = "FFFFFF", mainCategoryIconColor = "FFFFFF"),
                )
                defaultStockMarkets.forEach { mainCategoryDao.insertMainCategory(it) }
            }
        }

        private fun deleteDatabase(context: Context) {
            context.deleteDatabase("app_database")
        }
    }
}