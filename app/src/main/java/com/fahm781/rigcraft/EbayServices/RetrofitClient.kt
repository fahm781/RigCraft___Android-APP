package com.fahm781.rigcraft.EbayServices

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient  {
    private val BASE_URL = "https://api.ebay.com/"

    val ebayApi: EbayApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EbayApiService::class.java)
    }
}
