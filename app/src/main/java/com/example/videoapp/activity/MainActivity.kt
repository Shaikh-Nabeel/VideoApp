package com.example.videoapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoapp.adapters.VideoAdapter
import com.example.videoapp.databinding.ActivityMainBinding
import com.example.videoapp.interfaces.VideoClick
import com.example.videoapp.model.VidData
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class MainActivity : AppCompatActivity() , VideoClick{


    private lateinit var adapter: VideoAdapter
    private lateinit var binding: ActivityMainBinding
    val videoList =  mutableListOf<VidData>()
    val supabase = createSupabaseClient(
        supabaseUrl = "https://zdugmyolnkccvetuejiy.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpkdWdteW9sbmtjY3ZldHVlaml5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTQ5OTM5NDgsImV4cCI6MjAzMDU2OTk0OH0.FxT0Kia58HuxP-Qdd8ia_uY8FENj5S17BoUAuN2Ms3s"
    ) {
        install(Postgrest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        VideoList()

        binding.searchBtn.setOnClickListener{
            val search = binding.search.text.toString().trim()
            if(search.isNotEmpty()){
                searchVid(search)
            }
        }
    }

    private fun VideoList(){
        CoroutineScope(Dispatchers.IO).launch {
            val results = supabase.from("VideosData").select().decodeList<VidData>()
            videoList.addAll(results)
            runOnUiThread {
                adapter = VideoAdapter(this@MainActivity,videoList)
                binding.vidRV.layoutManager = LinearLayoutManager(applicationContext)
                binding.vidRV.adapter = adapter
            }
        }

    }

    private fun searchVid(search: String){
        CoroutineScope(Dispatchers.IO).launch {
            val results = supabase.from("VideosData").select {
                filter {
                    VidData::title ilike "%$search%"
                }
            }.decodeList<VidData>()

            runOnUiThread {
                adapter.updateList(results)
            }
        }

    }

    override fun onClick(data: VidData) {
        val jsonString = Json.encodeToString(data)
        val intent = Intent(this@MainActivity, VideoPlayActivity::class.java)
        intent.putExtra("data",jsonString)
        startActivity(intent)
    }
}

