package com.fahm781.rigcraft.ChatbotServices

import android.util.Log
import com.fahm781.rigcraft.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
//you can change API KEY HERE
interface OpenAiApiInterface {
    @POST("chat/completions")
    fun getResponse(
        @Body request: Request,
        @Header("Content-Type") token: String = "application/json",
        @Header("Authorization") authorization: String = "Bearer ${BuildConfig.OPEN_AI_API_KEY}"
    ): Call<Response>
    }


suspend fun getApiKey():  String?  {
    return try {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("OpenAIAPIKey").document("api_key").get().await()
         Log.d("Firestore", "Key is : ${snapshot.getString("key")}")
        snapshot.getString("key")
    } catch (e: Exception) {
        Log.d("Firestore", "Error getting documents: ", e)
        null
    }
}

