package app.skynier.skynier.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "account",
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val accountId: Int = 0, //帳戶編號
    val accountName: String, //帳戶名稱
    val accountCategoryId: Int, //帳戶分類編號 現金 信用卡 銀行
    val currency: String = "USD", //帳戶主幣別
    val initialBalance: Double, //帳戶初始餘額
    val note: String? = null, //帳戶備註
    val accountIcon: String, //帳戶圖示名稱
    var accountBackgroundColor: String, //帳戶背景顏色
    var accountIconColor: String, //帳戶圖示顏色
    var accountSort: Int //帳戶排序
)
