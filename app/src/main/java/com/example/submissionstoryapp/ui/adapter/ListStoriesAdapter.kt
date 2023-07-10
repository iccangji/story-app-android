package com.example.submissionstoryapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.local.entity.StoriesEntity
import com.example.submissionstoryapp.databinding.ItemStoriesBinding
import com.example.submissionstoryapp.ui.activity.DetailsStoryActivity
import com.example.submissionstoryapp.ui.activity.DetailsStoryActivity.Companion.STORY_ID

class ListStoriesAdapter():
    PagingDataAdapter<StoriesEntity, ListStoriesAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(private val binding: ItemStoriesBinding): RecyclerView.ViewHolder(binding.root) {
        private val tvName = binding.tvItemName
        private val ivPhoto = binding.ivItemPhoto
        fun bind(story: StoriesEntity){
            tvName.text = story.name
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(ivPhoto)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailsStoryActivity::class.java)
                intent.putExtra(STORY_ID, story.id)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(tvName, "name_story"),
                        Pair(ivPhoto, "photo_story")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoriesEntity>() {
            override fun areItemsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}