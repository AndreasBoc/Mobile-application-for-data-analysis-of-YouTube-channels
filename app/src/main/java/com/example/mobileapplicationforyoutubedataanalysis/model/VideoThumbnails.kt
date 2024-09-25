package com.example.mobileapplicationforyoutubedataanalysis.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VideoThumbnails (
    @SerializedName("high")
    val high: High

) {
    class High(
        @SerializedName("url")
        val url: String,
        @SerializedName("width")
        val width: Int,
        @SerializedName("height")
        val height: Int
    )
}