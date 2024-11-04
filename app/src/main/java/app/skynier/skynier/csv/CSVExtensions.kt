package app.skynier.skynier.csv

import app.skynier.skynier.database.entities.AccountCategoryEntity
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.CategoryEntity
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.SubCategoryEntity
import app.skynier.skynier.database.entities.UserSettingsEntity

fun AccountCategoryEntity.toCSVRow(): String {
    return "$accountCategoryId,$accountCategoryNameKey,$accountCategorySort"
}

fun AccountEntity.toCSVRow(): String {
    return "$accountId,$accountName,$accountCategoryId,$currency,$initialBalance,$note,$accountIcon,$accountBackgroundColor,$accountIconColor,$accountSort"
}

fun CategoryEntity.toCSVRow(): String {
    return "$categoryId,$categoryIdNameKey"
}

fun CurrencyEntity.toCSVRow(): String {
    return "$currencyId,$currency,$exchangeRate,$lastUpdatedTime"
}

fun MainCategoryEntity.toCSVRow(): String {
    return "$mainCategoryId,$categoryId,$mainCategoryNameKey,$mainCategoryIcon,$mainCategoryBackgroundColor,$mainCategoryIconColor,$mainCategorySort"
}

fun RecordEntity.toCSVRow(): String {
    return "$recordId,$accountId,$currency,$type,$categoryId,$mainCategoryId,$subCategoryId," +
            "$amount,$fee,$discount,$name,$merchant,$datetime,$description,$objectType"
}

fun SubCategoryEntity.toCSVRow(): String {
    return "$subCategoryId,$mainCategoryId,$subCategoryNameKey,$subCategoryIcon," +
            "$subCategoryBackgroundColor,$subCategoryIconColor,$subCategorySort"
}

fun UserSettingsEntity.toCSVRow(): String {
    return "$id,$themeIndex,$darkTheme,$currency,$textColor"
}