package app.skynier.skynier.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyEntity(
    @PrimaryKey(autoGenerate = true) val currencyId: Int = 0, //幣別編號
    val currency: String, // 幣別代碼，例如 USD, EUR
    val exchangeRate: Double,   // 匯率，例如 1.0, 0.85
    val lastUpdatedTime: Long? = null // 匯率價格最後更新時間
)
