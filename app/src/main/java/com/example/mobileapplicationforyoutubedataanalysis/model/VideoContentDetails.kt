package com.example.mobileapplicationforyoutubedataanalysis.model

import com.google.gson.annotations.SerializedName

data class VideoContentDetails (
    @SerializedName("duration")
    val duration: String
)