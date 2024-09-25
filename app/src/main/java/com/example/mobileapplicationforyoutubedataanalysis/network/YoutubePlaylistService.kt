package com.example.mobileapplicationforyoutubedataanalysis.network

import com.example.mobileapplicationforyoutubedataanalysis.model.PlaylistModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubePlaylistService {
    @GET("playlistItems")
    fun getPlaylistItems(
        @Query("part") part: String,
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") pageToken: String?,
        @Query("key") apiKey: String
    ): Call<PlaylistModel>
}



