package app.skynier.skynier.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.skynier.skynier.database.dao.AccountCategoryDao
import app.skynier.skynier.database.dao.AccountDao
import app.skynier.skynier.database.dao.CategoryDao
import app.skynier.skynier.database.dao.CurrencyDao
import app.skynier.skynier.database.dao.MainCategoryDao
import app.skynier.skynier.database.dao.SubCategoryDao
import app.skynier.skynier.database.dao.UserSettingsDao
import app.skynier.skynier.database.entities.AccountCategoryEntity
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.CategoryEntity
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.SubCategoryEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [AccountEntity::class,AccountCategoryEntity::class, CategoryEntity::class, CurrencyEntity::class, MainCategoryEntity::class, SubCategoryEntity::class, UserSettingsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun mainCategoryDao(): MainCategoryDao
    abstract fun subCategoryDao(): SubCategoryDao
    abstract fun accountCategoryDao(): AccountCategoryDao
    abstract fun userSettingsDao(): UserSettingsDao

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
                        populateDatabase(database.mainCategoryDao(), database.categoryDao(), database.subCategoryDao(), database.accountCategoryDao())
                    }
                }
            }

            suspend fun populateDatabase(mainCategoryDao: MainCategoryDao, categoryDao: CategoryDao, subCategoryDao: SubCategoryDao, accountCategoryDao: AccountCategoryDao) {
                if (categoryDao.getAllCategories().isEmpty()) {
                    val defaultCategory = listOf(
                        CategoryEntity(
                            categoryIdNameKey = "expense",
                        ),
                        CategoryEntity(
                            categoryIdNameKey = "income",
                        ),
                        CategoryEntity(
                            categoryIdNameKey = "transfer",
                        )
                    )
                    defaultCategory.forEach { categoryDao.insertCategory(it) }
                }
                if (accountCategoryDao.getAllAccountCategories().isEmpty()) {
                    val defaultAccountCategory = listOf(
                        AccountCategoryEntity(
                            accountCategoryNameKey = "account_category_cash",
                            accountCategorySort = 0
                        ),
                        AccountCategoryEntity(
                            accountCategoryNameKey = "account_category_bank",
                            accountCategorySort = 1
                        ),
                        AccountCategoryEntity(
                            accountCategoryNameKey = "account_category_credit_card",
                            accountCategorySort = 2
                        ),
                        AccountCategoryEntity(
                            accountCategoryNameKey = "account_category_securities",
                            accountCategorySort = 3
                        ),
                        AccountCategoryEntity(
                            accountCategoryNameKey = "account_category_uncategorized",
                            accountCategorySort = 4
                        ),
                    )
                    defaultAccountCategory.forEach { accountCategoryDao.insertAccountCategory(it) }
                }
                if (mainCategoryDao.getAllMainCategories().isEmpty()) {
                    val defaultMainCategory = listOf(
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_food",
                            mainCategoryIcon = "Restaurant",
                            mainCategoryBackgroundColor = "01B468",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 0,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_shopping",
                            mainCategoryIcon = "ShoppingCart",
                            mainCategoryBackgroundColor = "8C8C00",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 1,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_transportation",
                            mainCategoryIcon = "Commute",
                            mainCategoryBackgroundColor = "4C4414",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 2,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_travel",
                            mainCategoryIcon = "BeachAccess",
                            mainCategoryBackgroundColor = "23B0F0",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 3,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_activities",
                            mainCategoryIcon = "Snowboarding",
                            mainCategoryBackgroundColor = "7F7F84",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 4,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_finance",
                            mainCategoryIcon = "AccountBalance",
                            mainCategoryBackgroundColor = "B89E14",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 5,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_household",
                            mainCategoryIcon = "FamilyRestroom",
                            mainCategoryBackgroundColor = "7373B9",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 6,
                        ),
                        MainCategoryEntity(
                            categoryId = 2,
                            mainCategoryNameKey = "category_work",
                            mainCategoryIcon = "Work",
                            mainCategoryBackgroundColor = "DAA520",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 0,
                        ),
                        MainCategoryEntity(
                            categoryId = 2,
                            mainCategoryNameKey = "category_finance",
                            mainCategoryIcon = "AccountBalance",
                            mainCategoryBackgroundColor = "625B57",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 1,
                        ),
                        MainCategoryEntity(
                            categoryId = 3,
                            mainCategoryNameKey = "transfer",
                            mainCategoryIcon = "SyncAlt",
                            mainCategoryBackgroundColor = "3CB371",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 0,
                        ),
                    )
                    defaultMainCategory.forEach { mainCategoryDao.insertMainCategory(it) }
                }
                if (subCategoryDao.getAllSubCategories().isEmpty()) {
                    val defaultSubCategory = listOf(
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_breakfast",
                            subCategoryIcon = "BreakfastDining",
                            subCategoryBackgroundColor = "01B468",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_brunch",
                            subCategoryIcon = "BrunchDining",
                            subCategoryBackgroundColor = "01B468",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_lunch",
                            subCategoryIcon = "LunchDining",
                            subCategoryBackgroundColor = "01B468",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_dinner",
                            subCategoryIcon = "DinnerDining",
                            subCategoryBackgroundColor = "01B468",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_liquor",
                            subCategoryIcon = "Liquor",
                            subCategoryBackgroundColor = "01B468",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_snacks",
                            subCategoryIcon = "Cookie",
                            subCategoryBackgroundColor = "01B468",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_drinks",
                            subCategoryIcon = "LocalBar",
                            subCategoryBackgroundColor = "01B468",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 6
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_books",
                            subCategoryIcon = "MenuBook",
                            subCategoryBackgroundColor = "8C8C00",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_ebooks",
                            subCategoryIcon = "BookOnline",
                            subCategoryBackgroundColor = "8C8C00",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_video_games",
                            subCategoryIcon = "VideoGameAsset",
                            subCategoryBackgroundColor = "8C8C00",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_electronics",
                            subCategoryIcon = "Devices",
                            subCategoryBackgroundColor = "8C8C00",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_clothing",
                            subCategoryIcon = "Checkroom",
                            subCategoryBackgroundColor = "8C8C00",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_software",
                            subCategoryIcon = "GetApp",
                            subCategoryBackgroundColor = "8C8C00",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_air_ticket",
                            subCategoryIcon = "AirplaneTicket",
                            subCategoryBackgroundColor = "4C4414",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_parking_fee",
                            subCategoryIcon = "LocalParking",
                            subCategoryBackgroundColor = "4C4414",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_fuel_expense",
                            subCategoryIcon = "LocalGasStation",
                            subCategoryBackgroundColor = "4C4414",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_train",
                            subCategoryIcon = "Train",
                            subCategoryBackgroundColor = "4C4414",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_bus",
                            subCategoryIcon = "DirectionsBus",
                            subCategoryBackgroundColor = "4C4414",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_metro",
                            subCategoryIcon = "Subway",
                            subCategoryBackgroundColor = "4C4414",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_taxi",
                            subCategoryIcon = "LocalTaxi",
                            subCategoryBackgroundColor = "4C4414",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 6
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_ticket",
                            subCategoryIcon = "ConfirmationNumber",
                            subCategoryBackgroundColor = "23B0F0",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_hotel",
                            subCategoryIcon = "Hotel",
                            subCategoryBackgroundColor = "23B0F0",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_casino",
                            subCategoryIcon = "Casino",
                            subCategoryBackgroundColor = "23B0F0",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_attraction",
                            subCategoryIcon = "Attractions",
                            subCategoryBackgroundColor = "23B0F0",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_fitness_center",
                            subCategoryIcon = "FitnessCenter",
                            subCategoryBackgroundColor = "23B0F0",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_laundry_service",
                            subCategoryIcon = "LocalLaundryService",
                            subCategoryBackgroundColor = "23B0F0",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_theatre",
                            subCategoryIcon = "Theaters",
                            subCategoryBackgroundColor = "7F7F84",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_football",
                            subCategoryIcon = "SportsSoccer",
                            subCategoryBackgroundColor = "7F7F84",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_baseball",
                            subCategoryIcon = "SportsBaseball",
                            subCategoryBackgroundColor = "7F7F84",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_basketball",
                            subCategoryIcon = "SportsBasketball",
                            subCategoryBackgroundColor = "7F7F84",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_art",
                            subCategoryIcon = "Palette",
                            subCategoryBackgroundColor = "7F7F84",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_party",
                            subCategoryIcon = "Celebration",
                            subCategoryBackgroundColor = "7F7F84",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 6
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 6,
                            subCategoryNameKey = "category_stock",
                            subCategoryIcon = "TrendingDown",
                            subCategoryBackgroundColor = "B89E14",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 6,
                            subCategoryNameKey = "category_insurance",
                            subCategoryIcon = "Receipt",
                            subCategoryBackgroundColor = "B89E14",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 6,
                            subCategoryNameKey = "category_tax",
                            subCategoryIcon = "Payments",
                            subCategoryBackgroundColor = "B89E14",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_education",
                            subCategoryIcon = "School",
                            subCategoryBackgroundColor = "7373B9",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_gas_bill",
                            subCategoryIcon = "GasMeter",
                            subCategoryBackgroundColor = "7373B9",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_electricity_bill",
                            subCategoryIcon = "ElectricMeter",
                            subCategoryBackgroundColor = "7373B9",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_internet_bill",
                            subCategoryIcon = "Wifi",
                            subCategoryBackgroundColor = "7373B9",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_water_bill",
                            subCategoryIcon = "WaterDrop",
                            subCategoryBackgroundColor = "7373B9",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_medicine",
                            subCategoryIcon = "Vaccines",
                            subCategoryBackgroundColor = "7373B9",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_maintenance_fee",
                            subCategoryIcon = "Hardware",
                            subCategoryBackgroundColor = "7373B9",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 6
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_rent",
                            subCategoryIcon = "House",
                            subCategoryBackgroundColor = "7373B9",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 7
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 8,
                            subCategoryNameKey = "category_salary",
                            subCategoryIcon = "Paid",
                            subCategoryBackgroundColor = "DAA520",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 8,
                            subCategoryNameKey = "category_bonus",
                            subCategoryIcon = "Money",
                            subCategoryBackgroundColor = "DAA520",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 8,
                            subCategoryNameKey = "category_side_job",
                            subCategoryIcon = "Store",
                            subCategoryBackgroundColor = "DAA520",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 9 ,
                            subCategoryNameKey = "category_stock",
                            subCategoryIcon = "TrendingUp",
                            subCategoryBackgroundColor = "625B57",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 9 ,
                            subCategoryNameKey = "category_interest",
                            subCategoryIcon = "Savings",
                            subCategoryBackgroundColor = "625B57",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 9 ,
                            subCategoryNameKey = "category_investment",
                            subCategoryIcon = "Payments",
                            subCategoryBackgroundColor = "625B57",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 10 ,
                            subCategoryNameKey = "transfer",
                            subCategoryIcon = "SyncAlt",
                            subCategoryBackgroundColor = "3CB371",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 10 ,
                            subCategoryNameKey = "category_withdrawal",
                            subCategoryIcon = "Atm",
                            subCategoryBackgroundColor = "3CB371",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 10 ,
                            subCategoryNameKey = "category_deposit",
                            subCategoryIcon = "LocalAtm",
                            subCategoryBackgroundColor = "3CB371",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 10 ,
                            subCategoryNameKey = "category_exchange",
                            subCategoryIcon = "CurrencyExchange",
                            subCategoryBackgroundColor = "3CB371",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                    )
                    defaultSubCategory.forEach { subCategoryDao.insertSubCategory(it) }
                }
            }
        }

        private fun deleteDatabase(context: Context) {
            context.deleteDatabase("app_database")
        }
    }
}