package com.example.aptitude

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide





class YouTubeVideoAdapter(
    private var videos: List<VideoDataClass>,
    private val onItemClick: (VideoDataClass) -> Unit
) : RecyclerView.Adapter<YouTubeVideoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val channelName: TextView = itemView.findViewById(R.id.channelName) // New field
        private val viewCount: TextView = itemView.findViewById(R.id.viewCount) // New field
        private val publishedDate: TextView = itemView.findViewById(R.id.publishedDate) // New field

        fun bind(video: VideoDataClass) {
            Glide.with(itemView.context).load(video.thumbnailUrl).into(thumbnail)
            title.text = video.title
            channelName.text = "Channel: ${video.channelName}"
            viewCount.text = "\"${video.viewCount} views\""
            publishedDate.text = "Published: ${video.publishedAt}"

            itemView.setOnClickListener { onItemClick(video) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int = videos.size

    fun updateVideos(newVideos: List<VideoDataClass>) {
        Log.d("ADAPTER_UPDATE", "Updating with: $newVideos")
        videos = newVideos
        notifyDataSetChanged()
    }
}


