package com.example.mangiabastakotlin.Model.DataSources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ScreenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentScreenState(screenInfo: ScreenInfo);
    @Query("SELECT * FROM ScreenInfo WHERE ID=1")
    suspend fun getLastScreenState(): ScreenInfo?;
}