package com.example.aptitude

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
//AIzaSyAgw2chLlES1i146S6l11AuOxhaFwxwgR8
class MainViewModel : ViewModel() {
    private val apiKey = "AIzaSyCyagzTXXB1tMK7YcIdJyaa_gsXfaSgc4M"
    private val repository = YouTubeRepository(apiKey)

    private val _videos = MutableLiveData<List<VideoDataClass>>()
    val videos: LiveData<List<VideoDataClass>> get() = _videos


    init {
        loadDefaultVideos() // Load BTech-related videos on startup
    }

    private fun loadDefaultVideos() {
        viewModelScope.launch {
            try {
                val result = repository.searchVideos("BTech core subjects ")
                if (result.isNotEmpty()) {
                    _videos.postValue(result)
                }
            } catch (e: Exception) {
                Log.e("VIDEO_LOAD", "Error fetching default videos: ${e.message}")
            }
        }
    }

    fun searchVideos(query: String) {
        viewModelScope.launch {
            try {
                val result = repository.searchVideos(query)
                Log.d("VIEWMODEL", "Fetched ${result.size} videos")
                if (result.isNotEmpty()) {
                    _videos.postValue(result)
                }
            } catch (e: Exception) {
                Log.e("VIDEO_SEARCH", "Error fetching videos: ${e.message}")
            }
        }
    }
}

