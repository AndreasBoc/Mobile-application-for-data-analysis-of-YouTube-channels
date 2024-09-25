package com.example.mobileapplicationforyoutubedataanalysis.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class StatisticsYT (
    @SerializedName("viewCount")
    val viewCount: Int,

    @SerializedName("subscriberCount")
    val subscriberCount: Int,

    @SerializedName("videoCount")
    val videoCount: Int

)