package app.skynier.skynier.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val themeIndex: Int = 0, //主題顏色
    val darkTheme: Boolean = true,
    var currency: String = "USD", //主幣別
    val textColor: Int = 0, //收入支出文字顏色
)
