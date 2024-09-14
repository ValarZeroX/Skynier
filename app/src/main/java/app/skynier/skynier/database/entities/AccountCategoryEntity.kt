package app.skynier.skynier.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "account_category",
)
data class AccountCategoryEntity(
    @PrimaryKey(autoGenerate = true) val accountCategoryId: Int = 0, //帳戶分類編號
    val accountCategoryNameKey: String, //帳戶分類名稱
    var accountCategorySort: Int, //帳戶分類排序
)
