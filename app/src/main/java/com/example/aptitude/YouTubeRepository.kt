package com.example.aptitude

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//https://www.googleapis.com/youtube/v3/






class YouTubeRepository(private val apiKey: String) {
    private val apiService = YouTubeApiService.create()

    suspend fun searchVideos(query: String): List<VideoDataClass> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("REPO_CALL", "Fetching data for: $query")

                // Search for videos
                val searchResponse = apiService.searchVideos(query = query, apiKey = apiKey)
                val videoItems = searchResponse.items

                if (videoItems.isEmpty()) {
                    Log.d("REPO_CALL", "No videos found for: $query")
                    return@withContext emptyList()
                }

                // Extract video IDs
                val videoIds = videoItems.joinToString(",") { it.id.videoId }

                // Fetch statistics for these videos
                val statsResponse = apiService.getVideoDetails(videoIds = videoIds, apiKey = apiKey)
                val statsMap = statsResponse.items.associateBy { it.id }

                // Map response to VideoDataClass
                val videoList = videoItems.map { video ->
                    val statistics = statsMap[video.id.videoId]?.statistics
                    VideoDataClass(
                        videoId = video.id.videoId,
                        title = video.snippet.title,
                        thumbnailUrl = video.snippet.thumbnails.medium.url,
                        channelName = video.snippet.channelTitle ?: "Unknown Channel", // Channel name
                        viewCount = formatViewCount(statistics?.viewCount), // View count
                        publishedAt = video.snippet.publishedAt // Published date
                    )
                }

                Log.d("REPO_CALL", "Fetched ${videoList.size} videos successfully.")
                videoList
            } catch (e: HttpException) {
                Log.e("API_ERROR", "HTTP Error Code: ${e.code()} | Response: ${e.response()?.errorBody()?.string()}")
                emptyList()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Unknown Error: ${e.message}")
                emptyList()
            }
        }
    }
    private fun formatViewCount(count: String?): String {
        return try {
            val num = count?.toLongOrNull() ?: 0
            when {
                num >= 1_000_000_000 -> String.format("%.1fB", num / 1_000_000_000.0) // Billions
                num >= 1_000_000 -> String.format("%.1fM", num / 1_000_000.0) // Millions
                num >= 1_000 -> String.format("%.1fK", num / 1_000.0) // Thousands
                else -> num.toString() // Keep small numbers as-is
            }
        } catch (e: NumberFormatException) {
            "0"
        }
    }
}



