package com.example.mobileapplicationforyoutubedataanalysis.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ContentDetails (
    @SerializedName("relatedPlaylists")
    val relatedPlaylists: Uploads
) {
    class Uploads (
        @SerializedName("uploads")
        val uploads: String
    )
}