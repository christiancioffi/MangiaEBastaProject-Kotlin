package com.example.mangiabastakotlin.Model.DataSources

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MenuDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)    //Insert (or Update if the (old version of the) menu already exists)
    suspend fun insertMenu(menuEntities: MenuInfo);
    @Delete
    suspend fun deleteMenu(menuEntities: MenuInfo); //For debugging
    @Update
    suspend fun updateMenu(menuEntities: MenuInfo);
    @Query("SELECT * FROM MenuInfo WHERE mid = :mid AND imageVersion = :version")
    suspend fun getMenuImage(mid:Int, version: Int): MenuInfo?;

}