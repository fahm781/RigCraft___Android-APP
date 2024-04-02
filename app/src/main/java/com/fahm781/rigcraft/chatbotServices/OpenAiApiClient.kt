package com.fahm781.rigcraft.chatbotServices

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenAiApiClient {

    private var INSTANCE: OpenAiApiInterface? = null

    fun getInstance(): OpenAiApiInterface {
     synchronized(this) {
        return INSTANCE?: Retrofit.Builder()
       .baseUrl("https://api.openai.com/v1/")
       .addConverterFactory(GsonConverterFactory.create())
       .build()
       .create(OpenAiApiInterface::class.java)
       .also {
           INSTANCE = it
       }
     }
    }
}