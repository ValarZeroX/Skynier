package app.skynier.skynier.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "main_category",
)
data class MainCategoryEntity(
    @PrimaryKey val mainCategoryId: Int,
    val mainCategoryNameKey: String, // 主類別語系Key
    val mainCategoryIcon: String, //主類別圖示名稱
    var mainCategoryBackgroundColor: String, //主類別背景顏色
    var mainCategoryIconColor: String, //主類別圖示顏色
)
