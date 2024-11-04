package app.skynier.skynier.csv

import android.content.Context
import app.skynier.skynier.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun exportCSV(context: Context): File = withContext(Dispatchers.IO) {
    val database = AppDatabase.getDatabase(context)

    // Fetch all data from the database
    val accountCategories = database.accountCategoryDao().getAllAccountCategories()
    val accounts = database.accountDao().getAllAccounts()
    val categories = database.categoryDao().getAllCategories()
    val currencies = database.currencyDao().getAllCurrencies()
    val mainCategories = database.mainCategoryDao().getAllMainCategories()
    val records = database.recordDao().getAllRecordsSync()
    val subCategories = database.subCategoryDao().getAllSubCategories()
    val userSettings = database.userSettingsDao().getUserSettings()

    // Build CSV content
    val csvContent = StringBuilder()

    // AccountCategoryEntity
    csvContent.append("AccountCategoryEntity\n")
    csvContent.append("accountCategoryId,accountCategoryNameKey,accountCategorySort\n")
    accountCategories.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // AccountEntity
    csvContent.append("\nAccountEntity\n")
    csvContent.append("accountId,accountName,accountCategoryId,currency,initialBalance,note,accountIcon,accountBackgroundColor,accountIconColor,accountSort\n")
    accounts.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // CategoryEntity
    csvContent.append("\nCategoryEntity\n")
    csvContent.append("categoryId,categoryIdNameKey\n")
    categories.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // CurrencyEntity
    csvContent.append("\nCurrencyEntity\n")
    csvContent.append("currencyId,currency,exchangeRate,lastUpdatedTime\n")
    currencies.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // MainCategoryEntity
    csvContent.append("\nMainCategoryEntity\n")
    csvContent.append("mainCategoryId,categoryId,mainCategoryNameKey,mainCategoryIcon,mainCategoryBackgroundColor,mainCategoryIconColor,mainCategorySort\n")
    mainCategories.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // RecordEntity
    csvContent.append("\nRecordEntity\n")
    csvContent.append("recordId,accountId,currency,type,categoryId,mainCategoryId,subCategoryId,amount,fee,discount,name,merchant,datetime,description,objectType\n")
    records.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // SubCategoryEntity
    csvContent.append("\nSubCategoryEntity\n")
    csvContent.append("subCategoryId,mainCategoryId,subCategoryNameKey,subCategoryIcon,subCategoryBackgroundColor,subCategoryIconColor,subCategorySort\n")
    subCategories.forEach { csvContent.append(it.toCSVRow()).append("\n") }

    // UserSettingsEntity
    csvContent.append("\nUserSettingsEntity\n")
    csvContent.append("id,themeIndex,darkTheme,currency,textColor\n")
    userSettings?.let { csvContent.append(it.toCSVRow()).append("\n") }

    // Write to file
    val csvFile = File(context.getExternalFilesDir(null), "skynier_data.csv")
    csvFile.writeText(csvContent.toString())

    return@withContext csvFile
}