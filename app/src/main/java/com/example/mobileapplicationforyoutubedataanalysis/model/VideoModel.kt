package com.example.mobileapplicationforyoutubedataanalysis.model

import com.google.gson.annotations.SerializedName

data class VideoModel (
    @SerializedName("items")
    val items: List<Items>
) {
    data class Items (
        @SerializedName("id")
        val id: String,

        @SerializedName("snippet")
        val snippet: SnippetVideo,

        @SerializedName("contentDetails")
        val contentDetails: VideoContentDetails,

        @SerializedName("statistics")
        val statistics: StatisticsVideo
    )
}