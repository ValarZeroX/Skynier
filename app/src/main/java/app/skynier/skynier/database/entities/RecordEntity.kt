package app.skynier.skynier.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val recordId: Int = 0, // 記錄編號
    val accountId: Int, // 帳戶ID
    val currency: String, // 幣種代碼
    val categoryId: Int, // 記錄類型，例如"收入"或"支出"
    val mainCategoryId: Int, // 主類別ID
    val subCategoryId: Int, // 子類別ID
    val amount: Double, // 金額
    val fee: Double, // 手續費
    val discount: Double, // 折扣
    val name: String, // 名稱
    val merchant: String?, // 商家名稱
    val datetime: Long, // 記錄日期時間
    val description: String = "", // 描述
    val objectType: String? // 對象
)
