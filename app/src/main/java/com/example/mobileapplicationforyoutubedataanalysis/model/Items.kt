package com.example.mobileapplicationforyoutubedataanalysis.model

import com.google.gson.annotations.SerializedName

data class Items (
    @SerializedName("kind")
    val kind: String,

    @SerializedName("etag")
    val etag: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("contentDetails")
    val contentDetails: contentdetails,

    @SerializedName("statistics")
    val statistics : StatisticsVideo,

) {
    data class contentdetails (
        @SerializedName("videoId")
        val videoId: String,

        @SerializedName("videoPublishedAt")
        val videoPublishedAt: String
    )
}