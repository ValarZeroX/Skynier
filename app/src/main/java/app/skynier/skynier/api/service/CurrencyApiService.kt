package app.skynier.skynier.api.service

import app.skynier.skynier.api.response.CurrencyRatesResponse
import retrofit2.http.GET

interface CurrencyApiService {
    @GET("capi.php")
    suspend fun getCurrencyRates(): CurrencyRatesResponse
}
