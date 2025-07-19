package com.example.mangiabastakotlin.Model.DataSources

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MenuInfo(
    @PrimaryKey val mid: Int,
    val imageVersion: Int,
    val image: String,
)
