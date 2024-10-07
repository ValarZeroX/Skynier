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
import app.skynier.skynier.database.dao.RecordDao
import app.skynier.skynier.database.dao.SubCategoryDao
import app.skynier.skynier.database.dao.UserSettingsDao
import app.skynier.skynier.database.entities.AccountCategoryEntity
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.CategoryEntity
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.SubCategoryEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AccountEntity::class,
        AccountCategoryEntity::class,
        CategoryEntity::class,
        CurrencyEntity::class,
        MainCategoryEntity::class,
        SubCategoryEntity::class,
        UserSettingsEntity::class,
        RecordEntity::class
    ],
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
    abstract fun recordDao(): RecordDao

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
                        populateDatabase(
                            database.mainCategoryDao(),
                            database.categoryDao(),
                            database.subCategoryDao(),
                            database.accountCategoryDao()
                        )
                    }
                }
            }

            suspend fun populateDatabase(
                mainCategoryDao: MainCategoryDao,
                categoryDao: CategoryDao,
                subCategoryDao: SubCategoryDao,
                accountCategoryDao: AccountCategoryDao
            ) {
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
                            mainCategoryBackgroundColor = "EA00C962",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 0,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_shopping",
                            mainCategoryIcon = "ShoppingCart",
                            mainCategoryBackgroundColor = "FFFF2200",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 1,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_transportation",
                            mainCategoryIcon = "Commute",
                            mainCategoryBackgroundColor = "FF782B31",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 2,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_travel",
                            mainCategoryIcon = "BeachAccess",
                            mainCategoryBackgroundColor = "FF005CFF",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 3,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_activities",
                            mainCategoryIcon = "Snowboarding",
                            mainCategoryBackgroundColor = "FFD36AAB",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 4,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_finance",
                            mainCategoryIcon = "AccountBalance",
                            mainCategoryBackgroundColor = "FFFF3E4C",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 5,
                        ),
                        MainCategoryEntity(
                            categoryId = 1,
                            mainCategoryNameKey = "category_household",
                            mainCategoryIcon = "FamilyRestroom",
                            mainCategoryBackgroundColor = "FF9F9FFF",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 6,
                        ),
                        MainCategoryEntity(
                            categoryId = 2,
                            mainCategoryNameKey = "category_work",
                            mainCategoryIcon = "Work",
                            mainCategoryBackgroundColor = "FFD800FF",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 0,
                        ),
                        MainCategoryEntity(
                            categoryId = 2,
                            mainCategoryNameKey = "category_finance",
                            mainCategoryIcon = "AccountBalance",
                            mainCategoryBackgroundColor = "FFC66596",
                            mainCategoryIconColor = "FBFBFB",
                            mainCategorySort = 1,
                        ),
                        MainCategoryEntity(
                            categoryId = 3,
                            mainCategoryNameKey = "transfer",
                            mainCategoryIcon = "SyncAlt",
                            mainCategoryBackgroundColor = "FF39AB6C",
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
                            subCategoryBackgroundColor = "EA00C962",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_brunch",
                            subCategoryIcon = "BrunchDining",
                            subCategoryBackgroundColor = "EA00C962",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_lunch",
                            subCategoryIcon = "LunchDining",
                            subCategoryBackgroundColor = "EA00C962",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_dinner",
                            subCategoryIcon = "DinnerDining",
                            subCategoryBackgroundColor = "EA00C962",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_liquor",
                            subCategoryIcon = "Liquor",
                            subCategoryBackgroundColor = "EA00C962",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_snacks",
                            subCategoryIcon = "Cookie",
                            subCategoryBackgroundColor = "EA00C962",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 1,
                            subCategoryNameKey = "category_drinks",
                            subCategoryIcon = "LocalBar",
                            subCategoryBackgroundColor = "EA00C962",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 6
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_books",
                            subCategoryIcon = "MenuBook",
                            subCategoryBackgroundColor = "FFFF2200",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_ebooks",
                            subCategoryIcon = "BookOnline",
                            subCategoryBackgroundColor = "FFFF2200",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_video_games",
                            subCategoryIcon = "VideoGameAsset",
                            subCategoryBackgroundColor = "FFFF2200",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_electronics",
                            subCategoryIcon = "Devices",
                            subCategoryBackgroundColor = "FFFF2200",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_clothing",
                            subCategoryIcon = "Checkroom",
                            subCategoryBackgroundColor = "FFFF2200",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 2,
                            subCategoryNameKey = "category_software",
                            subCategoryIcon = "GetApp",
                            subCategoryBackgroundColor = "FFFF2200",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_air_ticket",
                            subCategoryIcon = "AirplaneTicket",
                            subCategoryBackgroundColor = "FF782B31",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_parking_fee",
                            subCategoryIcon = "LocalParking",
                            subCategoryBackgroundColor = "FF782B31",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_fuel_expense",
                            subCategoryIcon = "LocalGasStation",
                            subCategoryBackgroundColor = "FF782B31",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_train",
                            subCategoryIcon = "Train",
                            subCategoryBackgroundColor = "FF782B31",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_bus",
                            subCategoryIcon = "DirectionsBus",
                            subCategoryBackgroundColor = "FF782B31",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_metro",
                            subCategoryIcon = "Subway",
                            subCategoryBackgroundColor = "FF782B31",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 3,
                            subCategoryNameKey = "category_taxi",
                            subCategoryIcon = "LocalTaxi",
                            subCategoryBackgroundColor = "FF782B31",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 6
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_ticket",
                            subCategoryIcon = "ConfirmationNumber",
                            subCategoryBackgroundColor = "FF005CFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_hotel",
                            subCategoryIcon = "Hotel",
                            subCategoryBackgroundColor = "FF005CFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_casino",
                            subCategoryIcon = "Casino",
                            subCategoryBackgroundColor = "FF005CFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_attraction",
                            subCategoryIcon = "Attractions",
                            subCategoryBackgroundColor = "FF005CFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_fitness_center",
                            subCategoryIcon = "FitnessCenter",
                            subCategoryBackgroundColor = "FF005CFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 4,
                            subCategoryNameKey = "category_laundry_service",
                            subCategoryIcon = "LocalLaundryService",
                            subCategoryBackgroundColor = "FF005CFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_theatre",
                            subCategoryIcon = "Theaters",
                            subCategoryBackgroundColor = "FFD36AAB",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_football",
                            subCategoryIcon = "SportsSoccer",
                            subCategoryBackgroundColor = "FFD36AAB",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_baseball",
                            subCategoryIcon = "SportsBaseball",
                            subCategoryBackgroundColor = "FFD36AAB",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_basketball",
                            subCategoryIcon = "SportsBasketball",
                            subCategoryBackgroundColor = "FFD36AAB",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_art",
                            subCategoryIcon = "Palette",
                            subCategoryBackgroundColor = "FFD36AAB",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 5,
                            subCategoryNameKey = "category_party",
                            subCategoryIcon = "Celebration",
                            subCategoryBackgroundColor = "FFD36AAB",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 6
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 6,
                            subCategoryNameKey = "category_stock",
                            subCategoryIcon = "TrendingDown",
                            subCategoryBackgroundColor = "FFFF3E4C",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 6,
                            subCategoryNameKey = "category_insurance",
                            subCategoryIcon = "Receipt",
                            subCategoryBackgroundColor = "FFFF3E4C",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 6,
                            subCategoryNameKey = "category_tax",
                            subCategoryIcon = "Payments",
                            subCategoryBackgroundColor = "FFFF3E4C",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_education",
                            subCategoryIcon = "School",
                            subCategoryBackgroundColor = "FF9F9FFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_gas_bill",
                            subCategoryIcon = "GasMeter",
                            subCategoryBackgroundColor = "FF9F9FFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_electricity_bill",
                            subCategoryIcon = "ElectricMeter",
                            subCategoryBackgroundColor = "FF9F9FFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_internet_bill",
                            subCategoryIcon = "Wifi",
                            subCategoryBackgroundColor = "FF9F9FFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 3
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_water_bill",
                            subCategoryIcon = "WaterDrop",
                            subCategoryBackgroundColor = "FF9F9FFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 4
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_medicine",
                            subCategoryIcon = "Vaccines",
                            subCategoryBackgroundColor = "FF9F9FFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 5
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_maintenance_fee",
                            subCategoryIcon = "Hardware",
                            subCategoryBackgroundColor = "FF9F9FFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 6
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 7,
                            subCategoryNameKey = "category_rent",
                            subCategoryIcon = "House",
                            subCategoryBackgroundColor = "FF9F9FFF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 7
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 8,
                            subCategoryNameKey = "category_salary",
                            subCategoryIcon = "Paid",
                            subCategoryBackgroundColor = "FFD800FF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 8,
                            subCategoryNameKey = "category_bonus",
                            subCategoryIcon = "Money",
                            subCategoryBackgroundColor = "FFD800FF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 8,
                            subCategoryNameKey = "category_side_job",
                            subCategoryIcon = "Store",
                            subCategoryBackgroundColor = "FFD800FF",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 9,
                            subCategoryNameKey = "category_stock",
                            subCategoryIcon = "TrendingUp",
                            subCategoryBackgroundColor = "FFC66596",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 9,
                            subCategoryNameKey = "category_interest",
                            subCategoryIcon = "Savings",
                            subCategoryBackgroundColor = "FFC66596",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 9,
                            subCategoryNameKey = "category_investment",
                            subCategoryIcon = "Payments",
                            subCategoryBackgroundColor = "FFC66596",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 10,
                            subCategoryNameKey = "transfer",
                            subCategoryIcon = "SyncAlt",
                            subCategoryBackgroundColor = "FF39AB6C",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 0
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 10,
                            subCategoryNameKey = "category_withdrawal",
                            subCategoryIcon = "Atm",
                            subCategoryBackgroundColor = "FF39AB6C",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 1
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 10,
                            subCategoryNameKey = "category_deposit",
                            subCategoryIcon = "LocalAtm",
                            subCategoryBackgroundColor = "FF39AB6C",
                            subCategoryIconColor = "FBFBFB",
                            subCategorySort = 2
                        ),
                        SubCategoryEntity(
                            mainCategoryId = 10,
                            subCategoryNameKey = "category_exchange",
                            subCategoryIcon = "CurrencyExchange",
                            subCategoryBackgroundColor = "FF39AB6C",
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