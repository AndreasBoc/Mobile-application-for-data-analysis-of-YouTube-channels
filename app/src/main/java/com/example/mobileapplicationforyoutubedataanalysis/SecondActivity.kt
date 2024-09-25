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
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.Anchor
import com.anychart.enums.TooltipPositionMode
import com.bumptech.glide.Glide
import com.example.mobileapplicationforyoutubedataanalysis.model.ChannelModel
import com.example.mobileapplicationforyoutubedataanalysis.model.PlaylistModel
import com.example.mobileapplicationforyoutubedataanalysis.model.VideoModel
import com.example.mobileapplicationforyoutubedataanalysis.network.YoutubePlaylistService
import com.example.mobileapplicationforyoutubedataanalysis.network.YoutubeVideosService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class SecondActivity : AppCompatActivity() {
    private var allResponses = mutableListOf<PlaylistModel>()
    private var shortVideoCount = 0
    private var longVideoCount = 0

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val jsonContent = intent.getStringExtra("JSON_RESPONSE")
        val channelModel = Gson().fromJson(jsonContent, ChannelModel::class.java)

        val profileImage = findViewById<ImageView>(R.id.image_view_profile)
        val textViewTitle = findViewById<TextView>(R.id.text_view_title)
        val textViewViewCount = findViewById<TextView>(R.id.text_view_view_count)
        val textViewFrom = findViewById<TextView>(R.id.text_view_from)
        val textViewChannelCreated = findViewById<TextView>(R.id.text_view_published)
        val textViewSubscriberCount = findViewById<TextView>(R.id.text_view_subscriber_count)
        val textViewVideoCount = findViewById<TextView>(R.id.text_view_video_count)
        val lastVideoThumbnail = findViewById<ImageView>(R.id.last_video_thumbnail)
        val lastVideoDuration = findViewById<TextView>(R.id.last_video_duration)
        val lastVideoTitle = findViewById<TextView>(R.id.last_video_title)
        val lastVideoViews = findViewById<TextView>(R.id.last_video_views)
        val lastVideoLikes = findViewById<TextView>(R.id.last_video_likes)
        val lastVideoComments = findViewById<TextView>(R.id.last_video_comments)
        val lastVideoPublishedAt = findViewById<TextView>(R.id.last_video_published_at)
        val uploads = findViewById<Button>(R.id.btn_uploads)
        val comments = findViewById<Button>(R.id.btn_Comments)
        val analytics = findViewById<Button>(R.id.btn_analytics)
        val pieChartView = findViewById<AnyChartView>(R.id.pie_chart_view)

        val item = channelModel.items[0]
        val snippet = item.snippet
        val statistics = item.statistics
        val contentDetails = item.contentDetails

        textViewTitle.text = snippet.title
        textViewFrom.text = "Channel is based in: ${snippet.country}"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val channelDate = dateFormat.parse(snippet.publishedAt)
        val exitDateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val datePublished = exitDateFormat.format(channelDate!!)
        textViewChannelCreated.text = "Channel was created: $datePublished"

        val decimalFormat = DecimalFormat("#,###")
        val formattedViews = decimalFormat.format(statistics.viewCount)
        textViewViewCount.text = formattedViews
        val formattedSubscribers = decimalFormat.format(statistics.subscriberCount)
        textViewSubscriberCount.text = formattedSubscribers
        val formattedVideoCount = decimalFormat.format(statistics.videoCount)
        textViewVideoCount.text = "$formattedVideoCount videos"
        val profileImageUrl = snippet.thumbnails.high.url
        Glide.with(this)
            .load(profileImageUrl)
            .into(profileImage)
        val youtubeApiKey = intent.getStringExtra("API_KEY")

        uploads.setOnClickListener {
            val intent = Intent(this, UploadsActivity::class.java)
            intent.putExtra("playlistData", Gson().toJson(allResponses))
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title", textViewTitle.text)
            intent.putExtra("profilePicture", profileImageUrl)
            startActivity(intent)
        }

        analytics.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            intent.putExtra("playlistData", Gson().toJson(allResponses))
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title", textViewTitle.text)
            intent.putExtra("profilePicture", profileImageUrl)
            startActivity(intent)
        }

        comments.setOnClickListener {
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("playlistData", Gson().toJson(allResponses))
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title", textViewTitle.text)
            intent.putExtra("profilePicture", profileImageUrl)
            startActivity(intent)
        }

        val playlistId = contentDetails.relatedPlaylists.uploads
        var firstVideoId: String? = null
        var listOfVideoIds: List<String> = emptyList()

        getPlaylistData(youtubeApiKey ?: "", playlistId) { playlists, _ ->
            if (playlists != null) {
                println("${playlists.size} playlist.")
                allResponses.addAll(playlists)

                val videoIds = mutableListOf<String>()

                playlists.forEach { playlistModel ->
                    playlistModel.items.forEach { item ->
                        videoIds.add(item.contentDetails.videoId)
                    }
                }
                firstVideoId = videoIds.firstOrNull()
                listOfVideoIds = videoIds

            } else {
                println("No video IDs available.")
            }

            getVideoDetails(youtubeApiKey ?: "", firstVideoId ?: "") { videoModel ->
                if (videoModel != null) {
                    val title = videoModel.items.firstOrNull()?.snippet?.title
                    lastVideoTitle.text = "$title"

                    val thumbnailUrl =
                        videoModel.items.firstOrNull()?.snippet?.thumbnails?.high?.url
                    Glide.with(this)
                        .load(thumbnailUrl)
                        .into(lastVideoThumbnail)

                    val durationIso = videoModel.items.firstOrNull()?.contentDetails?.duration
                    val durationFormatted = convertIsoToVideoDuration(durationIso)
                    lastVideoDuration.text = durationFormatted

                    val views = videoModel.items.firstOrNull()?.statistics?.viewCount
                    lastVideoViews.text = "$views"

                    val likes = videoModel.items.firstOrNull()?.statistics?.likeCount
                    lastVideoLikes.text = "$likes"

                    val videocomments = videoModel.items.firstOrNull()?.statistics?.commentCount
                    lastVideoComments.text = "$videocomments"
                    val videodateFormat =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    videodateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val publishedAt = videoModel.items.firstOrNull()?.snippet?.publishedAt?.let {
                        dateFormat.parse(
                            it
                        )
                    }
                    val videoexitDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    lastVideoPublishedAt.text = videoexitDateFormat.format(publishedAt!!)

                } else {
                    println("Error retrieving details for first video.")
                }
            }

            getAllVideoDetails(youtubeApiKey ?: "", listOfVideoIds) { videoModelList ->
                for ((index, videoModel) in videoModelList.withIndex()) {
                    if (videoModel != null) {
                        val durationIso = videoModel.items.firstOrNull()?.contentDetails?.duration
                        val durationFormatted = convertIsoToVideoDuration(durationIso)
                        lastVideoDuration.text = durationFormatted
                        if (isVideoLong(durationFormatted)) {
                            longVideoCount++
                        }
                        if (isVideoShort(durationFormatted)) {
                            shortVideoCount++
                        }
                    } else {
                        println("Error retrieving details for video ${index + 1}.")
                    }
                }

                APIlib.getInstance().setActiveAnyChartView(pieChartView)
                val pie = AnyChart.pie()
                pie.data(
                    listOf(
                        ValueDataEntry("Short Videos", shortVideoCount),
                        ValueDataEntry("Long Videos", longVideoCount)
                    )
                )
                pie.title("Video number")
                pieChartView.setChart(pie)
                APIlib.getInstance().setActiveAnyChartView(pieChartView)
            }

            numberOfVideosByDay(youtubeApiKey ?: "", listOfVideoIds)
        }
    }

    private fun getPlaylistData(
        youtubeApiKey: String,
        playlistId: String,
        callback: (List<PlaylistModel>?, Throwable?) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(YoutubePlaylistService::class.java)

        val allResponses = mutableListOf<PlaylistModel>()
        var morePages = true
        var nextPageToken: String? = ""

        service.getPlaylistItems("contentDetails", playlistId, 50, nextPageToken,
            youtubeApiKey)

        val retrofitCallback = object : Callback<PlaylistModel> {
            override fun onResponse(call: Call<PlaylistModel>, response: Response<PlaylistModel>) {
                if (response.isSuccessful) {
                    val content = response.body()
                    content?.let {
                        allResponses.add(it)
                        nextPageToken = it.nextPageToken
                        morePages = nextPageToken != null
                    }
                    if (morePages) {
                        val callNextPage = service.getPlaylistItems(
                            "contentDetails",
                            playlistId,
                            50,
                            nextPageToken,
                            youtubeApiKey
                        )
                        callNextPage.enqueue(this)
                    } else {
                        callback(allResponses, null)
                    }
                } else {
                    val error =
                        Exception("Error retrieving data from YouTube API. Code: ${response.code()}")
                    callback(null, error)
                }
            }

            override fun onFailure(call: Call<PlaylistModel>, t: Throwable) {
                callback(null, t)
            }
        }
        val call =
            service.getPlaylistItems("contentDetails", playlistId, 50, nextPageToken,
                youtubeApiKey)
        call.enqueue(retrofitCallback)
    }

    private fun convertToDayOfWeek(dateStr: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(dateStr)
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dayFormat.format(date!!)
    }

    private fun numberOfVideosByDay(apiKey: String, videoIds: List<String>) {
        val dayOfWeekCounter = mutableMapOf(
            "Monday" to 0,
            "Tuesday" to 0,
            "Wednesday" to 0,
            "Thursday" to 0,
            "Friday" to 0,
            "Saturday" to 0,
            "Sunday" to 0
        )

        getAllVideoDetails(apiKey, videoIds) { videoModelList ->
            for (videoModel in videoModelList) {
                videoModel?.items?.firstOrNull()?.let { item ->
                    val publishedDate = item.snippet.publishedAt
                    val videoDayOfWeek = convertToDayOfWeek(publishedDate)
                    dayOfWeekCounter[videoDayOfWeek] =
                        dayOfWeekCounter.getOrDefault(videoDayOfWeek, 0) + 1
                }
            }

            val lineChartView = findViewById<AnyChartView>(R.id.line_chart_view)
            APIlib.getInstance().setActiveAnyChartView(lineChartView)

            val line = AnyChart.line()

            val data = listOf(
                ValueDataEntry("Mon", dayOfWeekCounter["Monday"]),
                ValueDataEntry("Tue", dayOfWeekCounter["Tuesday"]),
                ValueDataEntry("Wed", dayOfWeekCounter["Wednesday"]),
                ValueDataEntry("Thu", dayOfWeekCounter["Thursday"]),
                ValueDataEntry("Fri", dayOfWeekCounter["Friday"]),
                ValueDataEntry("Sat", dayOfWeekCounter["Saturday"]),
                ValueDataEntry("Sun", dayOfWeekCounter["Sunday"])
            )

            line.title("Number of videos uploaded by day")
            line.data(data)

            line.tooltip()
                .positionMode(TooltipPositionMode.POINT)
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5)
                .offsetY(5)

            lineChartView.setChart(line)

            dayOfWeekCounter.forEach { (day, count) ->
                println("Number of videos published on $day: $count")
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

                } else {
                    val error =
                        Exception("Error retrieving data from YouTube API. Code: ${response.code()}")
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

    private fun getAllVideoDetails(
        apiKey: String,
        videoIds: List<String>,
        callback: (List<VideoModel?>) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(YoutubeVideosService::class.java)
        val videoModelList = mutableListOf<VideoModel?>()

        for (videoId in videoIds) {
            val call = service.getVideoDetails("snippet,statistics,contentDetails", videoId, apiKey)
            call.enqueue(object : Callback<VideoModel> {
                override fun onResponse(call: Call<VideoModel>, response: Response<VideoModel>) {
                    if (response.isSuccessful) {
                        videoModelList.add(response.body())
                    } else {
                        println("Error retrieving data for video ID: $videoId, Code: ${response.code()}")
                        videoModelList.add(null)
                    }
                    if (videoModelList.size == videoIds.size) {
                        callback(videoModelList)
                    }
                }
                override fun onFailure(call: Call<VideoModel>, t: Throwable) {
                    println("Failure in network call for video ID: $videoId, ${t.message}")
                    videoModelList.add(null)
                    if (videoModelList.size == videoIds.size) {
                        callback(videoModelList)
                    }
                }
            })
        }
    }
}