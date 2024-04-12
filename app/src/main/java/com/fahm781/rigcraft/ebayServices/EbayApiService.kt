package com.fahm781.rigcraft.ebayServices


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query


interface EbayApiService {

    @Headers("Content-Type: application/json", "X-EBAY-C-MARKETPLACE-ID:EBAY_GB")
    @GET("buy/browse/v1/item_summary/search")
    fun searchItems(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("category_ids") categoryID: String,
        @Query("filter") filter: String

    ): Call<SearchResult>

    @Headers("Content-Type: application/json", "X-EBAY-C-MARKETPLACE-ID:EBAY_GB")
    @GET("buy/browse/v1/item_summary/search")
    fun searchItemsSortedByPrice(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("category_ids") categoryID: String,
        @Query("filter") filter: String,
        @Query("sort") sort: String = "price"
    ): Call<SearchResult>

}
