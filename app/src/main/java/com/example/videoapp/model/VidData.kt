package com.example.videoapp.model
import kotlinx.serialization.Serializable

@Serializable
data class VidData(
    val id: Int,
    val created_at: String,
    val likes: Int,
    val channel_name: String,
    val description: String,
    val title: String,
    val vid_url: String
)