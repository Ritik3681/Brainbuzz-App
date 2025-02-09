package com.example.aptitude

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



interface YouTubeApiService {

    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): YouTubeModels.YouTubeResponse

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "statistics",
        @Query("id") videoIds: String, // Comma-separated video IDs
        @Query("key") apiKey: String
    ): YouTubeModels.VideoDetailsResponse

    companion object {
        private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

        fun create(): YouTubeApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(YouTubeApiService::class.java)
        }
    }
}
