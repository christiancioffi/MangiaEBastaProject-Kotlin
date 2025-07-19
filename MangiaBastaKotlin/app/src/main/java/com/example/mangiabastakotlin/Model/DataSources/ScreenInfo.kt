package com.example.mangiabastakotlin.Model.DataSources

import androidx.room.Entity;
import androidx.room.PrimaryKey

@Entity
data class ScreenInfo(
    @PrimaryKey val id: Int = 1,
    val screen: String,
    val detailedMid: Int,
    val trackedOid: Int,
)