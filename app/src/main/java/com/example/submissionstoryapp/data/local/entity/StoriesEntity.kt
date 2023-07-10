package com.example.submissionstoryapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "stories")
class StoriesEntity(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    val id: String,


    @field:ColumnInfo(name = "photoUrl")
    val photoUrl: String,

    @field:ColumnInfo(name = "name")
    val name: String,

    @field:ColumnInfo(name = "lat")
    val lat: Double?,

    @field:ColumnInfo(name = "lon")
    val lon: Double?
)