package com.example.mangiabastakotlin.Model.Repositories

import android.util.Log
import com.example.mangiabastakotlin.Model.CreatedUser
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.CommunicationController
import com.example.mangiabastakotlin.Model.DataSources.DataStoreManager
import com.example.mangiabastakotlin.Model.DataSources.ScreenDao
import com.example.mangiabastakotlin.Model.DataSources.ScreenInfo
import com.example.mangiabastakotlin.Model.ScreenState

class ScreenRepository(database:AppDatabase, private val dsManager: DataStoreManager) {

    private val TAG = ScreenRepository::class.simpleName;
    private val communicationController:CommunicationController=CommunicationController();
    private val screenDao: ScreenDao = database.screenDao();


    suspend fun saveCurrentScreenState(screenState: ScreenState){
        val screenInfo= ScreenInfo(
            screen=screenState.screen,
            detailedMid=screenState.detailedMid,
            trackedOid=screenState.trackedOid
        )
        screenDao.insertCurrentScreenState(screenInfo);
    }

    suspend fun restoreLastScreenState(): ScreenState?{
        val screenInfo:ScreenInfo?=screenDao.getLastScreenState();
        if(screenInfo!=null){
            val screenState= ScreenState(
                screen=screenInfo.screen,
                detailedMid=screenInfo.detailedMid,
                trackedOid=screenInfo.trackedOid
            )
            return screenState;
        }else{
            return null;
        }
    }

    suspend fun configureSidUid() {
        val localSid:String?=dsManager.getSid();
        val localUid:Int?=dsManager.getUid();
        if(localSid==null || localUid==null){
            //Prima esecuzione
            val userCreatedResponse: CreatedUser = communicationController.createUser();
            val newSid=userCreatedResponse.sid;
            val newUid=userCreatedResponse.uid;
            dsManager.setSid(newSid);
            dsManager.setUid(newUid);
            CommunicationController.initializedSidUid(newSid,newUid);
            Log.d(TAG,"First execution. New SID: $newSid, New UID: $newUid");
        }else{
            //Non è la prima esecuzione
            Log.d(TAG,"Not the first execution. Local SID: $localSid, local UID: $localUid");
            CommunicationController.initializedSidUid(localSid,localUid);
        }

    }




}