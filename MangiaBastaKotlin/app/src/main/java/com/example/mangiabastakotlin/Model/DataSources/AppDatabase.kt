package com.example.mangiabastakotlin.Model.DataSources

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MenuInfo::class, ScreenInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
    abstract fun screenDao(): ScreenDao
}