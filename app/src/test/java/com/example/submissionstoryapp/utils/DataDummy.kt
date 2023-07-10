package com.example.submissionstoryapp.utils

import com.example.submissionstoryapp.data.local.entity.StoriesEntity

object DataDummy {
    fun generateDummyStoriesEntity(): List<StoriesEntity> {
        val storiesList = ArrayList<StoriesEntity>()
        for (i in 0..10) {
            val news = StoriesEntity(
                "$i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "ViewModelTesting $i",
                null,
                null
            )
            storiesList.add(news)
        }
        return storiesList
    }
}