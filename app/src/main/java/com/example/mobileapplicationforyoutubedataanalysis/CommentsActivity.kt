package com.example.mobileapplicationforyoutubedataanalysis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.mobileapplicationforyoutubedataanalysis.model.CommentThreadsModel
import com.example.mobileapplicationforyoutubedataanalysis.model.PlaylistModel
import com.example.mobileapplicationforyoutubedataanalysis.model.VideoModel
import com.example.mobileapplicationforyoutubedataanalysis.network.YoutubeComments
import com.example.mobileapplicationforyoutubedataanalysis.network.YoutubeVideosService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CommentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val playlistDataJson = intent.getStringExtra("playlistData")
        val playlistData = Gson().fromJson(playlistDataJson, Array<PlaylistModel>::class.java)
        val youtubeApiKey = intent.getStringExtra("API_KEY")
        val channelTitle = intent.getStringExtra("title")
        val profilePicture = intent.getStringExtra("profilePicture")
        val videoIds = mutableListOf<String>()
        println(playlistData)
        println(playlistDataJson)

        playlistData?.forEach { playlistModel ->
            playlistModel.items.forEach { item ->
                videoIds.add(item.contentDetails.videoId)
            }
        }
        println("List of Video IDs: $videoIds")

        val uploads = findViewById<Button>(R.id.btn_uploads)
        val analytics = findViewById<Button>(R.id.btn_analytics)
        val main = findViewById<Button>(R.id.btn_main)

        main.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("playlistData", playlistDataJson)
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title",channelTitle)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }

        uploads.setOnClickListener {
            val intent = Intent(this, UploadsActivity::class.java)
            intent.putExtra("playlistData", playlistDataJson)
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title",channelTitle)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }

        analytics.setOnClickListener{
            val intent = Intent(this, AnalyticsActivity::class.java)
            intent.putExtra("playlistData", playlistDataJson)
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title",channelTitle)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }

        val textViewTitle = findViewById<TextView>(R.id.text_view_title)
        textViewTitle.text=channelTitle
        val imageViewProfileImage = findViewById<ImageView>(R.id.image_view_profile)
        Glide.with(this)
            .load(profilePicture)
            .into(imageViewProfileImage)

        if (youtubeApiKey != null) {
            fetchCommentsForVideos(videoIds, youtubeApiKey)
        }
    }

    private fun fetchCommentsForVideos(videoIds: List<String>, apiKey: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(YoutubeComments::class.java)
        val container = findViewById<LinearLayout>(R.id.container_comments_layout)
        val inflater = LayoutInflater.from(this)

        for (videoId in videoIds) {
            val call = service.getCommentThreads("snippet", videoId, apiKey)
            call.enqueue(object : Callback<CommentThreadsModel> {
                val activityContext = this@CommentsActivity
                override fun onResponse(call: Call<CommentThreadsModel>, response: Response<CommentThreadsModel>) {
                    if (response.isSuccessful) {
                        val commentThreadsModel = response.body()!!
                        val commentsLayout = inflater.inflate(R.layout.comments_layout, container, false)
                        val videoTitle = commentsLayout.findViewById<TextView>(R.id.video_title)
                        val authorDisplayName = commentsLayout.findViewById<TextView>(R.id.authorDisplayName)
                        val authorProfilePicture = commentsLayout.findViewById<ImageView>(R.id.author_Profile_Picture)
                        val commenttext = commentsLayout.findViewById<TextView>(R.id.comment_text)
                        val commentlikes = commentsLayout.findViewById<TextView>(R.id.comment_like_count)
                        val commentpublishedAt = commentsLayout.findViewById<TextView>(R.id.comment_published_at)
                        for (commentThreadItem in commentThreadsModel.items) {
                            getVideoDetails(apiKey, videoId) { videoModel ->
                                if (videoModel != null) {
                                    val title = videoModel.items.firstOrNull()?.snippet?.title
                                    videoTitle.text = "$title"
                                }
                            }
                            val snippet = commentThreadItem.snippet.topLevelComment.snippet
                            val authorName = snippet.authorDisplayName
                            authorDisplayName.text = authorName
                            val authorPicture = snippet.authorProfileImageUrl
                            Glide.with(activityContext).load(authorPicture).into(authorProfilePicture)
                            val commentText = snippet.textDisplay
                            commenttext.text = commentText
                            val likecount = commentThreadItem.snippet.topLevelComment.snippet.likeCount
                            commentlikes.text = likecount.toString()
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                            val publishedAt = dateFormat.parse(snippet.publishedAt)
                            val exitDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            val datePublished = exitDateFormat.format(publishedAt!!)
                            commentpublishedAt.text = datePublished
                        }
                        container.addView(commentsLayout)
                    } else {
                        println("Error: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<CommentThreadsModel>, t: Throwable) {
                    println("Failure: ${t.message}")
                }
            })
        }
    }

    private fun getVideoDetails(apiKey: String, videoId: String, callback: (VideoModel?) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(YoutubeVideosService::class.java)

        val call = service.getVideoDetails("snippet,statistics,contentDetails", videoId, apiKey)
        call.enqueue(object : Callback<VideoModel> {
            override fun onResponse(call: Call<VideoModel>, response: Response<VideoModel>) {
                if (response.isSuccessful) {
                    callback(response.body())
                    //println("Video details successfully retrieved:")

                } else {
                    val error = Exception("Error retrieving data from YouTube API. Code: ${response.code()}")
                    callback(null)
                    println("Error: ${error.message}")
                }
            }
            override fun onFailure(call: Call<VideoModel>, t: Throwable) {
                val error = Exception("Failure in network call: ${t.message}")
                callback(null)
                println("Error: ${error.message}")
            }
        })
    }
}