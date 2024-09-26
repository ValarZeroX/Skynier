package app.skynier.skynier.api

import app.skynier.skynier.api.response.CurrencyRatesResponse
import app.skynier.skynier.api.service.CurrencyApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val currencyRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://tw.rter.info/") // Currency API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val currencyApi: CurrencyApiService by lazy {
        currencyRetrofit.create(CurrencyApiService::class.java)
    }

    suspend fun getCurrencyRates(): CurrencyRatesResponse {
        return currencyApi.getCurrencyRates()
    }
}