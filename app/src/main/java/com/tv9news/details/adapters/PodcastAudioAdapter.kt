package com.tv9news.details.adapters

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tv9news.R
import com.tv9news.databinding.ActivityDetailsBinding
import com.tv9news.details.activities.DetailsActivity
import com.tv9news.details.interfaces.DetailsItemClick
import com.tv9news.details.interfaces.OnPodcastClickListener
import com.tv9news.models.home.Articles
import com.tv9news.utils.helpers.Helper

class PodcastAudioAdapter(
    val context: Context,

    val jcAudioList: List<Articles>?,
    var detailsItemClick: DetailsItemClick,
    var mListener: OnPodcastClickListener
) :
    RecyclerView.Adapter<PodcastAudioAdapter.PodcastAudioAdapterViewHolder>() {
    private val progressMap = SparseArray<Float>()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PodcastAudioAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.podcast_audio_item, parent, false)
        return PodcastAudioAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcastAudioAdapterViewHolder, position: Int) {
        val title = jcAudioList!![position].article_title
        val image = jcAudioList!![position].article_image
        holder.audioTitle.text = title
        holder.itemView.tag = jcAudioList.get(position)
        if (image != null) {
            Helper.loadImage(holder.imageView, image, "")
        }

        holder.itemView.setOnClickListener(View.OnClickListener {
            if (mListener != null)
                mListener!!.onItemClick(position)
        })

        getToolbar().bookmarkIV.setOnClickListener(View.OnClickListener {
            detailsItemClick.BookmarkClick()
        })
    }

    fun getToolbar(): ActivityDetailsBinding {
        return (context as DetailsActivity).binding
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return jcAudioList?.size ?: 0
    }

    fun updateProgress(podcastAudio: Articles, progress: Float) {
        val position = jcAudioList!!.indexOf(podcastAudio)
        progressMap.put(position, progress)
        if (progressMap.size() > 1) {
            for (i in 0 until progressMap.size()) {
                if (progressMap.keyAt(i) != position) {
                    notifyItemChanged(progressMap.keyAt(i))
                    progressMap.delete(progressMap.keyAt(i))
                }
            }
        }
        notifyItemChanged(position)
    }

    class PodcastAudioAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val audioTitle: TextView
        val imageView: ImageView

        init {
            audioTitle = view.findViewById(R.id.titleTv)
            imageView = view.findViewById(R.id.imageView)
        }
    }
}