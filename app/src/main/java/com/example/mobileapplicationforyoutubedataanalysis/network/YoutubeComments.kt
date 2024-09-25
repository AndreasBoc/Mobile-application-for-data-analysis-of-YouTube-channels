package com.example.mobileapplicationforyoutubedataanalysis.network

import com.example.mobileapplicationforyoutubedataanalysis.model.CommentThreadsModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeComments {
    @GET("commentThreads")
    fun getCommentThreads(
        @Query("part") part: String,
        @Query("videoId") videoId: String,
        @Query("key") apiKey: String
    ): Call<CommentThreadsModel>
}

