package com.fahm781.rigcraft.EbayServices


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query


//interface EbayApiService {
//
////    val token: String
////        get() = "Bearer ${CoroutineScope(Dispatchers.Main).launch {EbayTokenRepo().getToken()}}"
//
//    @Headers("Authorization: Bearer {token}")
//    @GET("buy/browse/v1/item_summary/search")
//    fun searchItems(@Query("q") query: String): Call<SearchResult>
//
//
//}

interface EbayApiService {

    @Headers("Content-Type: application/json", "X-EBAY-C-MARKETPLACE-ID:EBAY_GB")
    @GET("buy/browse/v1/item_summary/search")
    fun searchItems(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("category_ids") categoryID: String,
        @Query("filter") filter: String
    ): Call<SearchResult>
}
