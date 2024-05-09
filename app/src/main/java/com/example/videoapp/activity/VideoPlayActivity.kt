package com.example.videoapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.videoapp.R
import com.example.videoapp.databinding.ActivityVideoPlayBinding
import com.example.videoapp.model.VidData
import kotlinx.serialization.json.Json

class VideoPlayActivity : AppCompatActivity() {

    lateinit var binding: ActivityVideoPlayBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getStringExtra("data")!!

        val vidData = Json.decodeFromString<VidData>(data)

        binding.title.text = vidData.title
        binding.description.text = vidData.description
        binding.channelLike.text = "${vidData.channel_name} • ${vidData.likes} Likes"

        binding.video.setVideoURI(Uri.parse(vidData.vid_url))
        binding.video.setOnPreparedListener {
            binding.vidProgress.visibility = View.GONE
            binding.video.visibility = View.VISIBLE
        }
        binding.video.start()
    }
}