package app.skynier.skynier.api.response

import com.google.gson.annotations.SerializedName

data class CurrencyRate(
    @SerializedName("Exrate") val exchangeRate: Double,
    @SerializedName("UTC") val utc: String
)

typealias CurrencyRatesResponse = Map<String, CurrencyRate>
