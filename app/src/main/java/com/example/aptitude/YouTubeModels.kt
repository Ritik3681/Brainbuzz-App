package com.example.aptitude

class YouTubeModels {
    data class YouTubeResponse(
        val nextPageToken: String?,
        val items: List<VideoItem>
    )

    data class VideoItem(
        val id: VideoId,
        val snippet: Snippet,
        val statistics: Statistics? // Added statistics field for view count
    )

    data class VideoId(
        val videoId: String
    )

    data class Snippet(
        val title: String,
        val thumbnails: Thumbnails,
        val channelTitle: String, // Added Channel Name
        val publishedAt: String // Added Published Date
    )

    data class Thumbnails(
        val medium: Thumbnail
    )

    data class Thumbnail(
        val url: String
    )
    data class VideoDetailsResponse(
        val items: List<VideoStatistics>
    )

    data class VideoStatistics(
        val id: String,
        val statistics: Statistics
    )
    data class Statistics(
        val viewCount: String
    )
}
