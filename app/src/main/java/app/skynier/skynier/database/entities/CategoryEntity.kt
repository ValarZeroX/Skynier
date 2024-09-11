package app.skynier.skynier.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "category",
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId: Int = 0, //類別編號
    val categoryIdNameKey: String, // 類別語系Key
)
