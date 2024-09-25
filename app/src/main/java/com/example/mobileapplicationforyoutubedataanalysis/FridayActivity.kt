package com.example.mobileapplicationforyoutubedataanalysis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.MarkerType
import com.bumptech.glide.Glide
import com.example.mobileapplicationforyoutubedataanalysis.model.PlaylistModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FridayActivity : AppCompatActivity() {
    private lateinit var anyChartView: AnyChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friday)

        anyChartView = findViewById(R.id.chart_container)

        val playlistDataJson = intent.getStringExtra("playlistData")
        val playlistData = Gson().fromJson(playlistDataJson, Array<PlaylistModel>::class.java)
        val youtubeApiKey = intent.getStringExtra("API_KEY")
        val channelTitle = intent.getStringExtra("title")
        val profilePicture = intent.getStringExtra("profilePicture")

        println(playlistData)
        println(playlistDataJson)

        val dataJson = intent.getStringExtra("data")
        val data = Gson().fromJson<Map<String, List<Long>>>(dataJson, object : TypeToken<Map<String, List<Long>>>() {}.type)

        println("Received data: $data")

        val textViewTitle = findViewById<TextView>(R.id.text_view_title)
        textViewTitle.text = channelTitle
        val imageViewProfileImage = findViewById<ImageView>(R.id.image_view_profile)
        Glide.with(this).load(profilePicture).into(imageViewProfileImage)

        setupScatterChart(data)

        val videoIds = mutableListOf<String>()
        playlistData?.forEach { playlistModel ->
            playlistModel.items.forEach { item ->
                videoIds.add(item.contentDetails.videoId)
            }
        }

        val back = findViewById<Button>(R.id.btn_back)
        val uploads = findViewById<Button>(R.id.btn_uploads)
        val analytics = findViewById<Button>(R.id.btn_analytics)
        val main = findViewById<Button>(R.id.btn_main)
        val comments = findViewById<Button>(R.id.btn_comments)

        back.setOnClickListener{
            val intent = Intent(this, AnalyticsActivity::class.java)
            intent.putExtra("playlistData", playlistDataJson)
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title",channelTitle)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }

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
            println("button is clicked")
            intent.putExtra("playlistData", playlistDataJson)
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title",channelTitle)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }

        comments.setOnClickListener {
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("playlistData", playlistDataJson)
            intent.putExtra("API_KEY", youtubeApiKey)
            intent.putExtra("title",channelTitle)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }
    }

    private fun setupScatterChart(data: Map<String, List<Long>>) {
        val scatter = AnyChart.scatter()

        scatter.title("Views by Published Time on Friday")
        scatter.xAxis(0).title("Published Time(h)")
        scatter.yAxis(0).title("Views")

        val dataEntries: MutableList<DataEntry> = mutableListOf()
        for ((time, viewsList) in data) {
            for (views in viewsList) {
                dataEntries.add(ValueDataEntry(time, views))
            }
        }

        val series = scatter.marker(dataEntries)
        series.type(MarkerType.CIRCLE)
        series.size(9.0)
        series.hovered().size(7.0)
        series.hovered().fill("#f48fb1")

        scatter.yScale().minimum(0)
        scatter.yScale().maximum(findMaxValue(data.values.flatten()))

        anyChartView.setChart(scatter)
    }

    private fun findMaxValue(values: List<Long>): Double {
        return values.maxOrNull()?.toDouble() ?: 0.0
    }
}