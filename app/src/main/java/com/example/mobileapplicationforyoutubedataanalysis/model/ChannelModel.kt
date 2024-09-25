package com.example.mobileapplicationforyoutubedataanalysis.model

import com.google.gson.annotations.SerializedName

data class ChannelModel (
    @SerializedName("items")
    val items: List<Items>
) {
    data class Items (
        @SerializedName("id")
        val id: String,

        @SerializedName("snippet")
        val snippet: SnippetYt,

        @SerializedName("contentDetails")
        val contentDetails: ContentDetails,

        @SerializedName("statistics")
        val statistics: StatisticsYT
    )
}