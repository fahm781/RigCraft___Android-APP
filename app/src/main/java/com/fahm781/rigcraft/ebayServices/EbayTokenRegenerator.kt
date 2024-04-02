package com.fahm781.rigcraft.ebayServices

import android.util.Base64
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.nio.charset.StandardCharsets

class EbayTokenRegenerator {

    fun CredToBase64(clientID:String, certID: String): String {
        return Base64.encodeToString((clientID + ":" +certID).toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)

    }

    suspend fun getToken(): String? = withContext(Dispatchers.IO) {

        // Define your credentials
        val clientId = ""   //hide these
        val clientSecret = ""     //hide these


        val encodedCredentials = CredToBase64(clientId, clientSecret)


        val client = OkHttpClient()


        val formBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("scope", "https://api.ebay.com/oauth/api_scope") // Replace with your actual scopes
            .build()


        val request = Request.Builder()
            .url("https://api.ebay.com/identity/v1/oauth2/token")
            .post(formBody)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Authorization", "Basic $encodedCredentials")
            .build()


        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            // Handle the response
            val responseBody = response.body()?.string()
            val jsonObject = JsonParser.parseString(responseBody).asJsonObject

            // Extract the access token
            return@use jsonObject.get("access_token").asString
        }
    }

}