package app.skynier.skynier.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_category",
)
data class SubCategoryEntity(
    @PrimaryKey(autoGenerate = true) val subCategoryId: Int = 0, //子類別編號
    val mainCategoryId: Int, //主類別編號
    val subCategoryNameKey: String, // 子類別語系Key
    val subCategoryIcon: String, //子類別圖示名稱
    var subCategoryBackgroundColor: String, //子類別背景顏色,暫時沒用
    var subCategoryIconColor: String, //子類別圖示顏色,暫時沒用
    var subCategorySort: Int, //子類別排序
)
