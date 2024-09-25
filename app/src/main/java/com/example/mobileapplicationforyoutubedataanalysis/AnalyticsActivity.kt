package com.example.mobileapplicationforyoutubedataanalysis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import java.util.Locale
import java.util.TimeZone
import com.bumptech.glide.Glide
import java.time.Duration

class AnalyticsActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        val playlistDataJson = intent.getStringExtra("playlistData")
        val playlistData = Gson().fromJson(playlistDataJson, Array<PlaylistModel>::class.java)
        val youtubeApiKey = intent.getStringExtra("API_KEY")
        val channelTitle = intent.getStringExtra("title")
        val profilePicture = intent.getStringExtra("profilePicture")
        println(playlistData)
        println(playlistDataJson)

        val uploads = findViewById<Button>(R.id.btn_uploads)
        val comments = findViewById<Button>(R.id.btn_Comments)
        val main = findViewById<Button>(R.id.btn_main)

        main.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            //intent.putExtra("playlistData", playlistDataJson)
            //intent.putExtra("API_KEY", youtubeApiKey)
            //intent.putExtra("title",channelTitle)
            //intent.putExtra("profilePicture", profilePicture)
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

        comments.setOnClickListener{
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("playlistData", playlistDataJson)
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title",channelTitle)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }

        val monbtn = findViewById<Button>(R.id.mon_btn)
        val tuebtn = findViewById<Button>(R.id.tue_btn)
        val wedbtn = findViewById<Button>(R.id.wed_btn)
        val thubtn = findViewById<Button>(R.id.thu_btn)
        val fribtn = findViewById<Button>(R.id.fri_btn)
        val satbtn = findViewById<Button>(R.id.sat_btn)
        val sunbtn = findViewById<Button>(R.id.sun_btn)

        val listOfVideoIds: List<String>
        val videoIds = mutableListOf<String>()
        playlistData?.forEach { playlistModel ->
            playlistModel.items.forEach { item ->
                videoIds.add(item.contentDetails.videoId)
            }
        }
        listOfVideoIds = videoIds

        monbtn.setOnClickListener {
            handleButtonClick(youtubeApiKey ?: "", videoIds, "Monday") { data ->
                val intent = Intent(this, MondayActivity::class.java)
                val dataJson = Gson().toJson(data)
                intent.putExtra("data", dataJson)
                intent.putExtra("API_KEY", youtubeApiKey)
                intent.putExtra("title", channelTitle)
                intent.putExtra("profilePicture", profilePicture)
                intent.putExtra("playlistData", playlistDataJson)
                startActivity(intent)
            }
        }

        tuebtn.setOnClickListener {
            handleButtonClick(youtubeApiKey ?: "", videoIds, "Tuesday") { data ->
                val intent = Intent(this, TuesdayActivity::class.java)
                val dataJson = Gson().toJson(data)
                intent.putExtra("data", dataJson)
                intent.putExtra("API_KEY", youtubeApiKey)
                intent.putExtra("title", channelTitle)
                intent.putExtra("profilePicture", profilePicture)
                intent.putExtra("playlistData", playlistDataJson)
                startActivity(intent)
            }
        }

        wedbtn.setOnClickListener {
            handleButtonClick(youtubeApiKey ?: "", videoIds, "Sunday") { data ->
                val intent = Intent(this, SundayActivity::class.java)
                val dataJson = Gson().toJson(data)
                intent.putExtra("data", dataJson)
                intent.putExtra("API_KEY", youtubeApiKey)
                intent.putExtra("title", channelTitle)
                intent.putExtra("profilePicture", profilePicture)
                intent.putExtra("playlistData", playlistDataJson)
                startActivity(intent)
            }
        }

        thubtn.setOnClickListener {
            handleButtonClick(youtubeApiKey ?: "", videoIds, "Thursday") { data ->
                val intent = Intent(this, ThursdayActivity::class.java)
                val dataJson = Gson().toJson(data)
                intent.putExtra("data", dataJson)
                intent.putExtra("API_KEY", youtubeApiKey)
                intent.putExtra("title", channelTitle)
                intent.putExtra("profilePicture", profilePicture)
                intent.putExtra("playlistData", playlistDataJson)
                startActivity(intent)
            }
        }

        fribtn.setOnClickListener {
            handleButtonClick(youtubeApiKey ?: "", videoIds, "Friday") { data ->
                val intent = Intent(this, FridayActivity::class.java)
                val dataJson = Gson().toJson(data)
                intent.putExtra("data", dataJson)
                intent.putExtra("API_KEY", youtubeApiKey)
                intent.putExtra("title", channelTitle)
                intent.putExtra("profilePicture", profilePicture)
                intent.putExtra("playlistData", playlistDataJson)
                startActivity(intent)
            }
        }

        satbtn.setOnClickListener {
            handleButtonClick(youtubeApiKey ?: "", videoIds, "Saturday") { data ->
                val intent = Intent(this, SaturdayActivity::class.java)
                val dataJson = Gson().toJson(data)
                intent.putExtra("data", dataJson)
                intent.putExtra("API_KEY", youtubeApiKey)
                intent.putExtra("title", channelTitle)
                intent.putExtra("profilePicture", profilePicture)
                intent.putExtra("playlistData", playlistDataJson)
                startActivity(intent)
            }
        }

        sunbtn.setOnClickListener {
            handleButtonClick(youtubeApiKey ?: "", videoIds, "Sunday") { data ->
                val intent = Intent(this, SundayActivity::class.java)
                val dataJson = Gson().toJson(data)
                intent.putExtra("data", dataJson)
                intent.putExtra("API_KEY", youtubeApiKey)
                intent.putExtra("title", channelTitle)
                intent.putExtra("profilePicture", profilePicture)
                intent.putExtra("playlistData", playlistDataJson)
                startActivity(intent)
            }
        }

        val textViewTitle = findViewById<TextView>(R.id.text_view_title)
        textViewTitle.text=channelTitle
        val imageViewProfileImage = findViewById<ImageView>(R.id.image_view_profile)
        Glide.with(this)
            .load(profilePicture)
            .into(imageViewProfileImage)

        getAllLongVideos(youtubeApiKey ?: "", listOfVideoIds) { longVideoModelList ->
            var maxViews = 0L
            var topVideoModel: VideoModel? = null
            for (videoModel in longVideoModelList) {
                val views = videoModel?.items?.firstOrNull()?.statistics?.viewCount?.toLongOrNull() ?: 0L
                if (views > maxViews) {
                    maxViews = views
                    topVideoModel = videoModel
                }
            }
            if (topVideoModel != null) {
                val durationIso = topVideoModel.items.firstOrNull()?.contentDetails?.duration
                val durationFormatted = convertIsoToVideoDuration(durationIso)
                val videoThumbnail = findViewById<ImageView>(R.id.long_video_thumbnail)
                val videoDuration = findViewById<TextView>(R.id.long_video_duration)
                val videoTitle = findViewById<TextView>(R.id.long_video_title)
                val videoViews = findViewById<TextView>(R.id.long_video_views)
                val videoLikes = findViewById<TextView>(R.id.long_video_likes)
                val videoComments = findViewById<TextView>(R.id.long_video_comments)
                val videoPublishedAt = findViewById<TextView>(R.id.long_video_published_at)
                val title = topVideoModel.items.firstOrNull()?.snippet?.title
                videoTitle.text = title ?: "Title not available"
                val thumbnailUrl = topVideoModel.items.firstOrNull()?.snippet?.thumbnails?.high?.url
                Glide.with(this).load(thumbnailUrl).into(videoThumbnail)
                videoDuration.text = durationFormatted
                videoViews.text = maxViews.toString()
                val likes = topVideoModel.items.firstOrNull()?.statistics?.likeCount
                videoLikes.text = likes ?: "Likes not available"
                val longcomments = topVideoModel.items.firstOrNull()?.statistics?.commentCount
                videoComments.text = longcomments ?: "Comments not available"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val publishedAt = topVideoModel.items.firstOrNull()?.snippet?.publishedAt?.let {
                    dateFormat.parse(
                        it
                    )
                }
                val exitDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                videoPublishedAt.text = exitDateFormat.format(publishedAt!!)
            } else {
                println("No video details available.")
            }
            val avgDuration = calculateAverageDuration(longVideoModelList)
            val avgDurationFormatted = formatDurationInMinutesAndSeconds(avgDuration)
            val averagelong = findViewById<TextView>(R.id.average_long)
            averagelong.text = avgDurationFormatted
        }

        getAllShortVideos(youtubeApiKey ?: "", listOfVideoIds) { shortVideoModelList ->
            var maxViews = 0L
            var topVideoModel: VideoModel? = null
            for (videoModel in shortVideoModelList) {
                val views = videoModel?.items?.firstOrNull()?.statistics?.viewCount?.toLongOrNull() ?: 0L
                if (views > maxViews) {
                    maxViews = views
                    topVideoModel = videoModel
                }
            }
            if (topVideoModel != null) {
                val durationIso = topVideoModel!!.items.firstOrNull()?.contentDetails?.duration
                val durationFormatted = convertIsoToVideoDuration(durationIso)
                val videoThumbnail = findViewById<ImageView>(R.id.short_video_thumbnail)
                val videoDuration = findViewById<TextView>(R.id.short_video_duration)
                val videoTitle = findViewById<TextView>(R.id.short_video_title)
                val videoViews = findViewById<TextView>(R.id.short_video_views)
                val videoLikes = findViewById<TextView>(R.id.short_video_likes)
                val videoComments = findViewById<TextView>(R.id.short_video_comments)
                val videoPublishedAt = findViewById<TextView>(R.id.short_video_published_at)
                val title = topVideoModel!!.items.firstOrNull()?.snippet?.title
                videoTitle.text = title ?: "Title not available"
                val thumbnailUrl = topVideoModel!!.items.firstOrNull()?.snippet?.thumbnails?.high?.url
                Glide.with(this).load(thumbnailUrl).into(videoThumbnail)
                videoDuration.text = durationFormatted
                videoViews.text = maxViews.toString()
                val likes = topVideoModel!!.items.firstOrNull()?.statistics?.likeCount
                videoLikes.text = likes ?: "Likes not available"
                val shortscomments = topVideoModel!!.items.firstOrNull()?.statistics?.commentCount
                videoComments.text = shortscomments ?: "Comments not available"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val publishedAt = topVideoModel!!.items.firstOrNull()?.snippet?.publishedAt?.let {
                    dateFormat.parse(
                        it
                    )
                }
                val exitDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                videoPublishedAt.text = exitDateFormat.format(publishedAt!!)
            } else {
                println("No video details available.")
            }
            val avgDuration = calculateAverageDuration(shortVideoModelList)
            val avgDurationFormatted = formatDurationInMinutesAndSeconds(avgDuration)
            val averageshort = findViewById<TextView>(R.id.average_shorts)
            averageshort.text = avgDurationFormatted
        }
    }
    private fun handleButtonClick(apiKey: String, videoIds: List<String>,
                                  dayOfWeek: String, callback: (Map<String, List<Long>>) -> Unit) {
        getAllVideos(apiKey, videoIds) { videoModelList ->
            val timeViewsMap = mutableMapOf<String, MutableList<Long>>()
            for (videoModel in videoModelList) {
                videoModel?.items?.firstOrNull()?.let { item ->
                    val publishedDate = item.snippet.publishedAt
                    val videoDayOfWeek = convertToDayOfWeek(publishedDate)
                    val timeOfDay = convertToTime(publishedDate)
                    val views = item.statistics.viewCount.toLongOrNull() ?: 0L
                    if (videoDayOfWeek == dayOfWeek) {
                        if (timeViewsMap.containsKey(timeOfDay)) {
                            timeViewsMap[timeOfDay]?.add(views)
                        } else {
                            timeViewsMap[timeOfDay] = mutableListOf(views)
                        }
                    }
                }
            }
            callback(timeViewsMap)
        }
    }

    private fun convertToDayOfWeek(dateStr: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(dateStr)
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dayFormat.format(date!!)
    }

    private fun convertToTime(dateStr: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(dateStr)
        val timeFormat = SimpleDateFormat("HH", Locale.getDefault())
        return timeFormat.format(date!!)
    }


    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertIsoToVideoDuration(isoDuration: String?): String {
        isoDuration?.let {
            val duration = Duration.parse(isoDuration)
            val minutes = duration.toMinutes()
            val seconds = duration.minusMinutes(minutes).seconds
            return String.format("%d:%02d", minutes, seconds)
        }
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertIsoToSeconds(isoDuration: String?): Long {
        isoDuration?.let {
            val duration = Duration.parse(isoDuration)
            return duration.seconds
        }
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAverageDuration(videoModelList: List<VideoModel?>): Long {
        var totalDuration = 0L
        var count = 0

        for (videoModel in videoModelList) {
            val durationIso = videoModel?.items?.firstOrNull()?.contentDetails?.duration
            totalDuration += convertIsoToSeconds(durationIso)
            count++
        }
        return if (count > 0) totalDuration / count else 0
    }

    @SuppressLint("DefaultLocale")
    private fun formatDurationInMinutesAndSeconds(durationInSeconds: Long): String {
        val minutes = durationInSeconds / 60
        val seconds = durationInSeconds % 60
        return "${minutes}:${String.format("%02d", seconds)}"
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
        val retrofit = Retrofit.Builder().baseUrl("https://www.googleapis.com/youtube/v3/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(YoutubeVideosService::class.java)
        val allResponses = mutableListOf<VideoModel?>()
        var completedRequests = 0

        for (videoId in videoIds) {
            val call = service.getVideoDetails("snippet,statistics,contentDetails", videoId, apiKey)
            call.enqueue(object : Callback<VideoModel> {
                override fun onResponse(call: Call<VideoModel>, response: Response<VideoModel>) {
                    completedRequests++
                    if (response.isSuccessful) {
                        allResponses.add(response.body())
                    } else {
                        allResponses.add(null)
                    }
                    if (completedRequests == videoIds.size) {
                        callback(allResponses)
                    }
                }
                override fun onFailure(call: Call<VideoModel>, t: Throwable) {
                    completedRequests++
                    allResponses.add(null)
                    if (completedRequests == videoIds.size) {
                        callback(allResponses)
                    }
                }
            })
        }
    }
}

