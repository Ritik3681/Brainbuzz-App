package com.example.aptitude

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class YouTubeRepository(private val apiKey: String) {
    private val apiService = YouTubeApiService.create()

    suspend fun searchVideos(query: String): List<VideoDataClass> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("REPO_CALL", "Fetching data for: $query")

                // ✅ Direct API Test URL
                val testUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=Ks-_Mh1QhMc&key=$apiKey"
                Log.e("API_TEST", "Testing API with URL: $testUrl")

                val allVideos = mutableListOf<VideoDataClass>()
                var nextPageToken: String? = null

                do {
                    val searchResponse = apiService.searchVideos(
                        query = query,
                        apiKey = apiKey,
                        pageToken = nextPageToken
                    )
                    Log.d("API_RESPONSE", "Raw Response: $searchResponse")
                    val videoItems = searchResponse.items

                    if (videoItems.isEmpty()) {
                        Log.d("REPO_CALL", "No videos found for: $query")
                        break
                    }

                    val videoIds = videoItems.joinToString(",") { it.id.videoId }

                    val statsResponse = apiService.getVideoDetails(videoIds = videoIds, apiKey = apiKey)
                    val statsMap = statsResponse.items.associateBy { it.id }

                    val videoList = videoItems.mapNotNull { video ->
                        val statistics = statsMap[video.id.videoId]?.statistics
                        val viewCount = statistics?.viewCount?.toLongOrNull() ?: return@mapNotNull null

                        VideoDataClass(
                            videoId = video.id.videoId,
                            title = video.snippet.title,
                            thumbnailUrl = video.snippet.thumbnails.medium.url,
                            channelName = video.snippet.channelTitle ?: "Unknown Channel",
                            viewCount = formatViewCount(viewCount.toString()),
                            publishedAt = video.snippet.publishedAt
                        )
                    }

                    allVideos.addAll(videoList)
                    nextPageToken = searchResponse.nextPageToken

                } while (!nextPageToken.isNullOrEmpty())

                Log.d("REPO_CALL", "Fetched ${allVideos.size} videos successfully.")
                return@withContext allVideos
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
                num >= 1_000_000_000 -> String.format("%.1fB", num / 1_000_000_000.0)
                num >= 1_000_000 -> String.format("%.1fM", num / 1_000_000.0)
                num >= 1_000 -> String.format("%.1fK", num / 1_000.0)
                else -> num.toString()
            }
        } catch (e: NumberFormatException) {
            "0"
        }
    }
}
