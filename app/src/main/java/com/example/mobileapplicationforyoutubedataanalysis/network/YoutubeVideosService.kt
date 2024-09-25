package com.example.mobileapplicationforyoutubedataanalysis.network

import com.example.mobileapplicationforyoutubedataanalysis.model.VideoModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeVideosService {
    @GET("videos")
    fun getVideoDetails(
        @Query("part") part: String,
        @Query("id") id: String,
        @Query("key") key: String
    ): Call<VideoModel>
}

