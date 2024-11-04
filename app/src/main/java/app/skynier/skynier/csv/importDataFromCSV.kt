package app.skynier.skynier.csv

import android.content.Context
import android.net.Uri
import app.skynier.skynier.database.AppDatabase
import app.skynier.skynier.database.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun importDataFromCSV(context: Context, csvFileUri: Uri) {
    withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(csvFileUri)?.bufferedReader()?.use { reader ->
            val lines = reader.readLines()
            var index = 0

            while (index < lines.size) {
                val line = lines[index].trim()
                when {
                    line == "AccountCategoryEntity" -> {
                        index = importAccountCategories(context, lines, index + 2)
                    }
                    line == "AccountEntity" -> {
                        index = importAccounts(context, lines, index + 2)
                    }
                    line == "CategoryEntity" -> {
                        index = importCategories(context, lines, index + 2)
                    }
                    line == "CurrencyEntity" -> {
                        index = importCurrencies(context, lines, index + 2)
                    }
                    line == "MainCategoryEntity" -> {
                        index = importMainCategories(context, lines, index + 2)
                    }
                    line == "RecordEntity" -> {
                        index = importRecords(context, lines, index + 2)
                    }
                    line == "SubCategoryEntity" -> {
                        index = importSubCategories(context, lines, index + 2)
                    }
                    line == "UserSettingsEntity" -> {
                        index = importUserSettings(context, lines, index + 2)
                    }
                    else -> index++
                }
            }
        }
    }
}

// Implement import functions for each entity
private suspend fun importAccountCategories(context: Context, lines: List<String>, startIndex: Int): Int {
    val accountCategories = mutableListOf<AccountCategoryEntity>()
    var index = startIndex

    while (index < lines.size && lines[index].isNotBlank()) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 3) {
            val entity = AccountCategoryEntity(
                accountCategoryId = tokens[0].toInt(),
                accountCategoryNameKey = tokens[1],
                accountCategorySort = tokens[2].toInt()
            )
            accountCategories.add(entity)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    database.accountCategoryDao().insertAccountCategories(accountCategories)
    return index
}

private suspend fun importAccounts(context: Context, lines: List<String>, startIndex: Int): Int {
    val accounts = mutableListOf<AccountEntity>()
    var index = startIndex

    while (index < lines.size && lines[index].isNotBlank()) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 10) {
            val entity = AccountEntity(
                accountId = tokens[0].toInt(),
                accountName = tokens[1],
                accountCategoryId = tokens[2].toInt(),
                currency = tokens[3],
                initialBalance = tokens[4].toDouble(),
                note = tokens[5].takeIf { it.isNotEmpty() },
                accountIcon = tokens[6],
                accountBackgroundColor = tokens[7],
                accountIconColor = tokens[8],
                accountSort = tokens[9].toInt()
            )
            accounts.add(entity)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    database.accountDao().insertAccounts(accounts)
    return index
}

private suspend fun importCategories(context: Context, lines: List<String>, startIndex: Int): Int {
    val categories = mutableListOf<CategoryEntity>()
    var index = startIndex

    while (index < lines.size && lines[index].isNotBlank()) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 2) {
            val entity = CategoryEntity(
                categoryId = tokens[0].toInt(),
                categoryIdNameKey = tokens[1]
            )
            categories.add(entity)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    database.categoryDao().insertCategories(categories)
    return index
}

private suspend fun importCurrencies(context: Context, lines: List<String>, startIndex: Int): Int {
    val currencies = mutableListOf<CurrencyEntity>()
    var index = startIndex

    while (index < lines.size && lines[index].isNotBlank()) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 4) {
            val entity = CurrencyEntity(
                currencyId = tokens[0].toInt(),
                currency = tokens[1],
                exchangeRate = tokens[2].toDouble(),
                lastUpdatedTime = tokens[3].toLongOrNull()
            )
            currencies.add(entity)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    database.currencyDao().insertCurrencies(currencies)
    return index
}

private suspend fun importMainCategories(context: Context, lines: List<String>, startIndex: Int): Int {
    val mainCategories = mutableListOf<MainCategoryEntity>()
    var index = startIndex

    while (index < lines.size && lines[index].isNotBlank()) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 7) {
            val entity = MainCategoryEntity(
                mainCategoryId = tokens[0].toInt(),
                categoryId = tokens[1].toInt(),
                mainCategoryNameKey = tokens[2],
                mainCategoryIcon = tokens[3],
                mainCategoryBackgroundColor = tokens[4],
                mainCategoryIconColor = tokens[5],
                mainCategorySort = tokens[6].toInt()
            )
            mainCategories.add(entity)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    database.mainCategoryDao().insertMainCategories(mainCategories)
    return index
}

private suspend fun importRecords(context: Context, lines: List<String>, startIndex: Int): Int {
    val records = mutableListOf<RecordEntity>()
    var index = startIndex

    while (index < lines.size && lines[index].isNotBlank()) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 15) {
            val entity = RecordEntity(
                recordId = tokens[0].toInt(),
                accountId = tokens[1].toInt(),
                currency = tokens[2],
                type = tokens[3].toInt(),
                categoryId = tokens[4].toInt(),
                mainCategoryId = tokens[5].toInt(),
                subCategoryId = tokens[6].toInt(),
                amount = tokens[7].toDouble(),
                fee = tokens[8].toDouble(),
                discount = tokens[9].toDouble(),
                name = tokens[10],
                merchant = tokens[11].takeIf { it.isNotEmpty() },
                datetime = tokens[12].toLong(),
                description = tokens[13],
                objectType = tokens[14].takeIf { it.isNotEmpty() }
            )
            records.add(entity)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    database.recordDao().insertRecords(records)
    return index
}

private suspend fun importSubCategories(context: Context, lines: List<String>, startIndex: Int): Int {
    val subCategories = mutableListOf<SubCategoryEntity>()
    var index = startIndex

    while (index < lines.size && lines[index].isNotBlank()) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 7) {
            val entity = SubCategoryEntity(
                subCategoryId = tokens[0].toInt(),
                mainCategoryId = tokens[1].toInt(),
                subCategoryNameKey = tokens[2],
                subCategoryIcon = tokens[3],
                subCategoryBackgroundColor = tokens[4],
                subCategoryIconColor = tokens[5],
                subCategorySort = tokens[6].toInt()
            )
            subCategories.add(entity)
        }
        index++
    }

    val database = AppDatabase.getDatabase(context)
    database.subCategoryDao().insertSubCategories(subCategories)
    return index
}

private suspend fun importUserSettings(context: Context, lines: List<String>, startIndex: Int): Int {
    var index = startIndex

    if (index < lines.size && lines[index].isNotBlank()) {
        val tokens = lines[index].split(",").map { it.trim() }
        if (tokens.size == 5) {
            val entity = UserSettingsEntity(
                id = tokens[0].toInt(),
                themeIndex = tokens[1].toInt(),
                darkTheme = tokens[2].toBoolean(),
                currency = tokens[3],
                textColor = tokens[4].toInt()
            )

            val database = AppDatabase.getDatabase(context)
            database.userSettingsDao().insertUserSettings(entity)
        }
        index++
    }

    return index
}