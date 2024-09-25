package com.example.mobileapplicationforyoutubedataanalysis.model

import com.google.gson.annotations.SerializedName


data class SnippetVideo (
        @SerializedName("publishedAt")
        val publishedAt: String,

        @SerializedName("title")
        val title: String,

        @SerializedName("description")
        val description: String,

        @SerializedName("thumbnails")
        val thumbnails: VideoThumbnails
)