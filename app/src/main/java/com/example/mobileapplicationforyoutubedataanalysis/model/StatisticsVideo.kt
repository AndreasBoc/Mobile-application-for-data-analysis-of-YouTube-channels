package com.example.mobileapplicationforyoutubedataanalysis.model

import com.google.gson.annotations.SerializedName

data class StatisticsVideo (
    @SerializedName("viewCount")
    val viewCount: String,

    @SerializedName("likeCount")
    val likeCount: String,

    @SerializedName("commentCount")
    val commentCount: String
)