package com.example.videoapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R
import com.example.videoapp.interfaces.VideoClick
import com.example.videoapp.model.VidData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


class VideoAdapter(private val listener: VideoClick, var listOfVid : MutableList<VidData>) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private val thumbnailCache = LruCache<String, Bitmap>(4 * 1024 * 1024)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        val progressBar: ProgressBar = itemView.findViewById(R.id.vidProgress)
        val title: TextView = itemView.findViewById(R.id.title)
        val channelNLike: TextView = itemView.findViewById(R.id.channel_like)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.video_rv_items,parent,false))
    }

    override fun getItemCount(): Int {
        return listOfVid.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listOfVid[position]
        holder.title.text = data.title
        holder.channelNLike.text = "${data.channel_name} • ${data.likes} Likes"
        val thumbnail = thumbnailCache.get(data.vid_url)

        if(thumbnail != null){
            showThumbnail(holder, thumbnail)
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                getThumbnail(data.vid_url).apply {
                    CoroutineScope(Dispatchers.Main).launch {
                        showThumbnail(holder,this@apply)
                    }
                }
            }
        }
        holder.itemView.setOnClickListener {
            listener.onClick(data)
        }
    }

    private fun showThumbnail(holder: ViewHolder, thumbnail: Bitmap){
        holder.progressBar.visibility = View.GONE
        holder.thumbnail.visibility = View.VISIBLE
        holder.thumbnail.setImageBitmap(thumbnail)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<VidData>){
        listOfVid.clear()
        listOfVid.addAll(list)
        notifyDataSetChanged()
    }

    fun getThumbnail(url: String): Bitmap{
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(url, HashMap<String, String>())
            val thumbnail = retriever.frameAtTime!!
            thumbnailCache.put(url, thumbnail)
            thumbnail
        } catch (e: IOException) {
            Log.e("thumbnailERR", "Failed to retrieve video thumbnail", e)
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        } finally {
            retriever.release()
        }
    }

}