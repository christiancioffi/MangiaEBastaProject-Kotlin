package com.example.mangiabastakotlin.Model.Repositories

import com.example.mangiabastakotlin.Model.DataSources.CommunicationController
import com.example.mangiabastakotlin.Model.UserDetails

class UserRepository {
    private val TAG = UserRepository::class.simpleName;
    private val communicationController= CommunicationController();

    suspend fun getUser(): UserDetails {
        return communicationController.getUser();
    }

    suspend fun sendDetails(userDetails: UserDetails){
        return communicationController.updateUser(userDetails);
    }
}