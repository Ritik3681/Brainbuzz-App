package com.example.aptitude

data class VideoDataClass(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val channelName: String, // Added Channel Name
    val viewCount: String, // Added View Count
    val publishedAt: String // Added Published Date
)
