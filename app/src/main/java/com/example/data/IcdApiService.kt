package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import android.util.Log

@JsonClass(generateAdapter = true)
data class IcdTokenResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "expires_in") val expiresIn: Int,
    @Json(name = "token_type") val tokenType: String
)

@JsonClass(generateAdapter = true)
data class IcdSearchResponse(
    @Json(name = "destinationEntities") val destinationEntities: List<IcdSearchEntity>? = null,
    @Json(name = "wordSuggestion") val wordSuggestion: String? = null
)

@JsonClass(generateAdapter = true)
data class IcdSearchEntity(
    @Json(name = "id") val uri: String, // e.g. "https://id.who.int/icd/release/11/2024-01/mms/979471374"
    @Json(name = "title") val title: String, // e.g. "Depressive Episode"
    @Json(name = "theCode") val theCode: String? = null, // e.g. "6A70"
    @Json(name = "matchingText") val matchingText: String? = null,
    @Json(name = "definition") val definition: String? = null
) {
    val id: String
        get() = uri.substringAfterLast("/")
}

interface IcdAuthService {
    @FormUrlEncoded
    @POST("connect/token")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("scope") scope: String = "icdapi_access",
        @Field("grant_type") grantType: String = "client_credentials"
    ): IcdTokenResponse
}

interface IcdSearchService {
    @GET("icd/release/11/2024-01/mms/search")
    suspend fun searchIcd(
        @Header("Authorization") bearerToken: String,
        @Header("API-Version") apiVersion: String = "v2",
        @Header("Accept-Language") acceptLanguage: String = "de",
        @Query("q") query: String
    ): IcdSearchResponse
}

class IcdApiRepository {
    private val tag = "IcdApiRepository"
    private var cachedToken: String? = null
    private var tokenExpiryTime: Long = 0

    private val moshi = com.squareup.moshi.Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val authService: IcdAuthService by lazy {
        Retrofit.Builder()
            .baseUrl("https://icdaccessmanagement.who.int/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(IcdAuthService::class.java)
    }

    private val searchService: IcdSearchService by lazy {
        Retrofit.Builder()
            .baseUrl("https://id.who.int/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(IcdSearchService::class.java)
    }

    private suspend fun getOrRefreshToken(clientId: String, clientSecret: String): String? {
        val now = System.currentTimeMillis()
        if (cachedToken != null && now < tokenExpiryTime) {
            return cachedToken
        }

        try {
            Log.d(tag, "Fetching OAuth2 token from WHO...")
            val response = authService.getAccessToken(clientId, clientSecret)
            cachedToken = "Bearer " + response.accessToken
            tokenExpiryTime = now + (response.expiresIn * 1000L) - 30000L // 30s buffer
            return cachedToken
        } catch (e: Exception) {
            Log.e(tag, "Failed to authenticate with WHO ICD API: ${e.message}", e)
            return null
        }
    }

    suspend fun searchWebIcd(clientId: String, clientSecret: String, query: String): List<IcdSearchEntity> {
        if (clientId.isEmpty() || clientSecret.isEmpty() || clientId.contains("MY_") || clientSecret.contains("MY_")) {
            Log.w(tag, "WHO credentials are empty or default placeholder, skipping web request.")
            return emptyList()
        }
        val token = getOrRefreshToken(clientId, clientSecret) ?: return emptyList()
        return try {
            Log.d(tag, "Querying ICD-11 Live search with: $query")
            val results = searchService.searchIcd(bearerToken = token, query = query)
            results.destinationEntities ?: emptyList()
        } catch (e: Exception) {
            Log.e(tag, "Failed to query ICD-11 Live API: ${e.message}", e)
            emptyList()
        }
    }
}
