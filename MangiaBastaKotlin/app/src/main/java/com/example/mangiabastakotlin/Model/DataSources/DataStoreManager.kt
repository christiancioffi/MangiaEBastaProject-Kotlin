package com.example.mangiabastakotlin.Model.DataSources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    /*suspend fun getLastScreen():String{
        val data=dataStore.data.first();
        val SCREEN_KEY = stringPreferencesKey("lastScreen");
        val screen:String?=data[SCREEN_KEY];
         return screen;
    }
    suspend fun setLastScreen(screen: String){
        val SCREEN_KEY = stringPreferencesKey("lastScreen");
        dataStore.edit { preferences ->
            preferences[SCREEN_KEY]=screen;
        }
    }*/
    /*
    suspend fun getLastDetailedMid():Int?{
        val data=dataStore.data.first();
        val MID_KEY = intPreferencesKey("lastDetailedMid");
        val mid:Int?=data[MID_KEY];
        return mid;
    }
    suspend fun setLastDetailedMid(mid: Int){
        val MID_KEY = intPreferencesKey("lastDetailedMid");
        dataStore.edit { preferences ->
            preferences[MID_KEY]=mid;
        }
    }
    suspend fun getLastTrackedOid():Int?{
        val data=dataStore.data.first();
        val OID_KEY = intPreferencesKey("lastTrackedOid");
        val oid:Int?=data[OID_KEY];
        return oid;
    }
    suspend fun setLastTrackedOid(oid: Int){
        val OID_KEY = intPreferencesKey("lastTrackedOid");
        dataStore.edit { preferences ->
            preferences[OID_KEY]=oid;
        }
    }*/

    suspend fun getSid():String?{
        val data=dataStore.data.first();
        val SID_KEY = stringPreferencesKey("sid");
        val sid:String?=data[SID_KEY]
        return sid;
    }

    suspend fun getUid():Int?{
        val data=dataStore.data.first();
        val UID_KEY = intPreferencesKey("uid");
        val uid:Int?=data[UID_KEY];
        return uid;
    }

    suspend fun setSid(sid: String){
        val SID_KEY = stringPreferencesKey("sid");
        dataStore.edit { preferences ->
            preferences[SID_KEY]=sid;
        }
    }

    suspend fun setUid(uid: Int){
        val UID_KEY = intPreferencesKey("uid");
        dataStore.edit { preferences ->
            preferences[UID_KEY]=uid;
        }
    }

}