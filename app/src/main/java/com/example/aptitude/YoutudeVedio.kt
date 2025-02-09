package com.example.aptitude

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class YoutudeVedio : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: YouTubeVideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = YouTubeVideoAdapter(emptyList()) { video ->
            playVideo(this, video.videoId)
        }
        recyclerView.adapter = adapter

        viewModel.videos.observe(this) { videos ->
            adapter.updateVideos(videos)
        }

        val searchButton = findViewById<Button>(R.id.searchButton)
        val searchInput = findViewById<EditText>(R.id.searchInput)

        searchButton.setOnClickListener {
            val query = searchInput.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchVideos(query)
            }
        }
    }

    private fun playVideo(context: Context, videoId: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
        context.startActivity(intent)
    }
}

