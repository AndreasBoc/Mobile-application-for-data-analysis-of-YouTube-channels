package com.example.mobileapplicationforyoutubedataanalysis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mobileapplicationforyoutubedataanalysis.model.PlaylistModel
import com.example.mobileapplicationforyoutubedataanalysis.model.VideoModel
import com.example.mobileapplicationforyoutubedataanalysis.network.YoutubeVideosService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Locale
import java.util.TimeZone


class UploadsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var shortsbutton : Button
    private lateinit var longbutton : Button

    //@SuppressLint("CutPasteId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uploads)

        val playlistDataJson = intent.getStringExtra("playlistData")
        val playlistData = Gson().fromJson(playlistDataJson, Array<PlaylistModel>::class.java)
        val youtubeApiKey = intent.getStringExtra("API_KEY")
        val channelTitle = intent.getStringExtra("title")
        val profilePicture = intent.getStringExtra("profilePicture")


        val videoIds = mutableListOf<String>()
        shortsbutton = findViewById(R.id.shorts_button)
        longbutton = findViewById(R.id.long_button)


        val textViewTitle = findViewById<TextView>(R.id.text_view_title)
        textViewTitle.text=channelTitle
        val imageViewProfileImage = findViewById<ImageView>(R.id.image_view_profile)
        Glide.with(this)
            .load(profilePicture)
            .into(imageViewProfileImage)

        playlistData?.forEach { playlistModel ->
            playlistModel.items.forEach { item ->
                videoIds.add(item.contentDetails.videoId)
            }
        }
        println("List of Video IDs: $videoIds")
        val listOfVideoIds: List<String> = videoIds

        val container = findViewById<LinearLayout>(R.id.container_layout)
        val inflater = LayoutInflater.from(this)

        val main = findViewById<Button>(R.id.btn_main)
        val comments = findViewById<Button>(R.id.btn_Comments)
        val analytics = findViewById<Button>(R.id.btn_analytics)

        main.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("playlistData", playlistDataJson)
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title",channelTitle)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }

        analytics.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            intent.putExtra("playlistData", playlistDataJson)
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title",channelTitle)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }

        comments.setOnClickListener{
            val intent = Intent(this, CommentsActivity::class.java)
            println("button is clicked")
            intent.putExtra("playlistData", playlistDataJson)
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title",channelTitle)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }

        shortsbutton.setOnClickListener {
            getAllShortVideos(youtubeApiKey ?: "", listOfVideoIds) { shortVideoModelList ->
                container.removeAllViews()
                for (videoModel in shortVideoModelList) {
                    if (videoModel != null) {
                        val durationIso = videoModel.items.firstOrNull()?.contentDetails?.duration
                        val durationFormatted = convertIsoToVideoDuration(durationIso)
                        val videoLayout = inflater.inflate(R.layout.video_item_layout, container, false)
                        val videoThumbnail = videoLayout.findViewById<ImageView>(R.id.video_thumbnail)
                        val videoDuration = videoLayout.findViewById<TextView>(R.id.video_duration)
                        val videoTitle = videoLayout.findViewById<TextView>(R.id.video_title)
                        val videoViews = videoLayout.findViewById<TextView>(R.id.video_views)
                        val videoLikes = videoLayout.findViewById<TextView>(R.id.video_likes)
                        val videoComments = videoLayout.findViewById<TextView>(R.id.video_comments)
                        val videoPublishedAt = videoLayout.findViewById<TextView>(R.id.video_published_at)

                        val title = videoModel.items.firstOrNull()?.snippet?.title
                        videoTitle.text = title ?: "Title not available"
                        val thumbnailUrl = videoModel.items.firstOrNull()?.snippet?.thumbnails?.high?.url
                        Glide.with(this).load(thumbnailUrl).into(videoThumbnail)
                        videoDuration.text = durationFormatted
                        val views = videoModel.items.firstOrNull()?.statistics?.viewCount
                        videoViews.text = views ?: "Views not available"
                        val likes = videoModel.items.firstOrNull()?.statistics?.likeCount
                        videoLikes.text = likes ?: "Likes not available"
                        val shortscomments = videoModel.items.firstOrNull()?.statistics?.commentCount
                        videoComments.text = shortscomments ?: "Comments not available"

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val publishedAt =
                            videoModel.items.firstOrNull()?.snippet?.publishedAt?.let { it1 ->
                                dateFormat.parse(
                                    it1
                                )
                            }
                        val exitDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        videoPublishedAt.text = exitDateFormat.format(publishedAt!!)

                        container.addView(videoLayout)

                        } else {
                            println("Error retrieving details for a video.")
                        }
                    }
                }
        }
        getAllLongVideos(youtubeApiKey ?: "", listOfVideoIds) { longVideoModelList ->
            container.removeAllViews()
            for (videoModel in longVideoModelList) {
                if (videoModel != null) {
                    val durationIso = videoModel.items.firstOrNull()?.contentDetails?.duration
                    val durationFormatted = convertIsoToVideoDuration(durationIso)
                    val videoLayout = inflater.inflate(R.layout.video_item_layout, container, false)
                    val videoThumbnail = videoLayout.findViewById<ImageView>(R.id.video_thumbnail)
                    val videoDuration = videoLayout.findViewById<TextView>(R.id.video_duration)
                    val videoTitle = videoLayout.findViewById<TextView>(R.id.video_title)
                    val videoViews = videoLayout.findViewById<TextView>(R.id.video_views)
                    val videoLikes = videoLayout.findViewById<TextView>(R.id.video_likes)
                    val videoComments = videoLayout.findViewById<TextView>(R.id.video_comments)
                    val videoPublishedAt = videoLayout.findViewById<TextView>(R.id.video_published_at)

                    val title = videoModel.items.firstOrNull()?.snippet?.title
                    videoTitle.text = title ?: "Title not available"
                    val thumbnailUrl = videoModel.items.firstOrNull()?.snippet?.thumbnails?.high?.url
                    Glide.with(this).load(thumbnailUrl).into(videoThumbnail)
                    videoDuration.text = durationFormatted
                    val views = videoModel.items.firstOrNull()?.statistics?.viewCount
                    videoViews.text = views ?: "Views not available"
                    val likes = videoModel.items.firstOrNull()?.statistics?.likeCount
                    videoLikes.text = likes ?: "Likes not available"
                    val longcomments = videoModel.items.firstOrNull()?.statistics?.commentCount
                    videoComments.text = longcomments ?: "Comments not available"
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val publishedAt = videoModel.items.firstOrNull()?.snippet?.publishedAt?.let {
                        dateFormat.parse(
                            it
                        )
                    }
                    val exitDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    videoPublishedAt.text = exitDateFormat.format(publishedAt!!)
                    container.addView(videoLayout)

                } else {
                    println("Error retrieving details for a video.")
                }
            }
        }

        longbutton.setOnClickListener {
            getAllLongVideos(youtubeApiKey ?: "", listOfVideoIds) { longVideoModelList ->
                container.removeAllViews()
                for (videoModel in longVideoModelList) {
                    if (videoModel != null) {
                        val durationIso = videoModel.items.firstOrNull()?.contentDetails?.duration
                        val durationFormatted = convertIsoToVideoDuration(durationIso)
                        val videoLayout = inflater.inflate(R.layout.video_item_layout, container, false)
                        val videoThumbnail = videoLayout.findViewById<ImageView>(R.id.video_thumbnail)
                        val videoDuration = videoLayout.findViewById<TextView>(R.id.video_duration)
                        val videoTitle = videoLayout.findViewById<TextView>(R.id.video_title)
                        val videoViews = videoLayout.findViewById<TextView>(R.id.video_views)
                        val videoLikes = videoLayout.findViewById<TextView>(R.id.video_likes)
                        val videoComments = videoLayout.findViewById<TextView>(R.id.video_comments)
                        val videoPublishedAt = videoLayout.findViewById<TextView>(R.id.video_published_at)

                        val title = videoModel.items.firstOrNull()?.snippet?.title
                        videoTitle.text = title ?: "Title not available"
                        val thumbnailUrl = videoModel.items.firstOrNull()?.snippet?.thumbnails?.high?.url
                        Glide.with(this).load(thumbnailUrl).into(videoThumbnail)
                        videoDuration.text = durationFormatted
                        val views = videoModel.items.firstOrNull()?.statistics?.viewCount
                        videoViews.text = views ?: "Views not available"
                        val likes = videoModel.items.firstOrNull()?.statistics?.likeCount
                        videoLikes.text = likes ?: "Likes not available"
                        val longcomments = videoModel.items.firstOrNull()?.statistics?.commentCount
                        videoComments.text = longcomments ?: "Comments not available"
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val publishedAt =
                            videoModel.items.firstOrNull()?.snippet?.publishedAt?.let { it1 ->
                                dateFormat.parse(
                                    it1
                                )
                            }
                        val exitDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        videoPublishedAt.text = exitDateFormat.format(publishedAt!!)
                        container.addView(videoLayout)

                        } else {
                            println("Error retrieving details for a video.")
                        }
                    }
                }
            }
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.shorts_button ->{
            }
            R.id.long_button ->{
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertIsoToVideoDuration(isoDuration: String?): String {
        isoDuration?.let {
            val duration = Duration.parse(isoDuration)
            val minutes = duration.toMinutes()
            val seconds = duration.minusMinutes(minutes).seconds
            return "${minutes}:${String.format("%02d", seconds)}"
        }
        return ""
    }

    private fun isVideoLong(durationFormatted: String): Boolean {
        if (durationFormatted.isNotEmpty()) {
            val parts = durationFormatted.split(":")
            if (parts.size == 2) {
                val minutes = parts[0].toIntOrNull() ?: 0
                val seconds = parts[1].toIntOrNull() ?: 0
                if (minutes >= 2 && seconds >= 1) {
                    return true
                }
            }
        }
        return false
    }

    private fun isVideoShort(durationFormatted: String): Boolean {
        if (durationFormatted.isNotEmpty()) {
            val parts = durationFormatted.split(":")
            if (parts.size == 2) {
                val minutes = parts[0].toIntOrNull() ?: 0
                val seconds = parts[1].toIntOrNull() ?: 0
                if (minutes == 0 && seconds <= 59) {
                    return true
                }
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllShortVideos(apiKey: String, videoIds: List<String>, callback: (List<VideoModel?>) -> Unit) {
        getAllVideos(apiKey, videoIds) { videoModelList ->
            callback(videoModelList.filter { videoModel ->
                val durationIso = videoModel?.items?.firstOrNull()?.contentDetails?.duration
                val durationFormatted = convertIsoToVideoDuration(durationIso)
                isVideoShort(durationFormatted)
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllLongVideos(apiKey: String, videoIds: List<String>, callback: (List<VideoModel?>) -> Unit) {
        getAllVideos(apiKey, videoIds) { videoModelList ->
            callback(videoModelList.filter { videoModel ->
                val durationIso = videoModel?.items?.firstOrNull()?.contentDetails?.duration
                val durationFormatted = convertIsoToVideoDuration(durationIso)
                isVideoLong(durationFormatted)
            })
        }
    }

    private fun getAllVideos(apiKey: String, videoIds: List<String>, callback: (List<VideoModel?>) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(YoutubeVideosService::class.java)

        val allResponses = mutableListOf<VideoModel?>()
        var counter = 0

        for (videoId in videoIds) {
            val call = service.getVideoDetails("snippet,statistics,contentDetails", videoId, apiKey)
            call.enqueue(object : Callback<VideoModel> {
                override fun onResponse(call: Call<VideoModel>, response: Response<VideoModel>) {
                    if (response.isSuccessful) {
                        allResponses.add(response.body())
                    } else {
                        println("Error retrieving data for video ID: $videoId, Code: ${response.code()}")
                        allResponses.add(null)
                    }
                    counter++
                    if (counter == videoIds.size) {
                        callback(allResponses)
                    }
                }

                override fun onFailure(call: Call<VideoModel>, t: Throwable) {
                    println("Failure in network call for video ID: $videoId, ${t.message}")
                    allResponses.add(null)
                    counter++
                    if (counter == videoIds.size) {
                        callback(allResponses)
                    }
                }
            })
        }
    }
}