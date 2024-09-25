package com.example.mobileapplicationforyoutubedataanalysis.network

import com.example.mobileapplicationforyoutubedataanalysis.model.ChannelModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeService {
    @GET("channels")
    fun getChannelData(
        @Query("part") part: String,
        @Query("id") id: String,
        @Query("key") key: String
    ): Call<ChannelModel>
}





