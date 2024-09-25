package com.example.mobileapplicationforyoutubedataanalysis.model

import com.google.gson.annotations.SerializedName

data class CommentThreadsModel(
    @SerializedName("items")
    val items: List<CommentThreadItem>
) {
    data class CommentThreadItem(
        @SerializedName("snippet")
        val snippet: Snippet
    ) {
        data class Snippet(
            @SerializedName("videoId")
            val videoId: String,

            @SerializedName("topLevelComment")
            val topLevelComment: TopLevelComment
        ) {
            data class TopLevelComment(
                @SerializedName("snippet")
                val snippet: CommentSnippet
            ) {
                data class CommentSnippet(
                    @SerializedName("authorDisplayName")
                    val authorDisplayName: String,

                    @SerializedName("authorProfileImageUrl")
                    val authorProfileImageUrl: String,

                    @SerializedName("textDisplay")
                    val textDisplay: String,

                    @SerializedName("likeCount")
                    val likeCount: Int,

                    @SerializedName("publishedAt")
                    val publishedAt: String
                )
            }
        }
    }
}
