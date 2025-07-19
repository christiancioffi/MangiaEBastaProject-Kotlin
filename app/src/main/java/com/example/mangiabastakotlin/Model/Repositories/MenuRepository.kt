package com.example.mangiabastakotlin.Model.Repositories

import android.util.Log
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.CommunicationController
import com.example.mangiabastakotlin.Model.DataSources.MenuInfo
import com.example.mangiabastakotlin.Model.Location
import com.example.mangiabastakotlin.Model.MenuLongDetails
import com.example.mangiabastakotlin.Model.MenuShortDetails
import com.example.mangiabastakotlin.Model.OrderDetails

class MenuRepository(database: AppDatabase) {

    private val TAG = MenuRepository::class.simpleName;
    private val communicationController= CommunicationController();
    private val menuDao = database.menuDao();

    suspend fun getMenuByLocation(location: Location): List<MenuShortDetails>{
        return communicationController.getMenuByLocation(location);
    }

    suspend fun getMenuDetails(mid:Int, location: Location): MenuLongDetails {
        return communicationController.getMenuDetails(mid, location);
    }

    suspend fun orderMenu(mid:Int, location: Location): OrderDetails {
        val user=communicationController.getUser();
        user.canOrder();
        Log.d(TAG, "User can order");
        return communicationController.buyMenu(mid,location);
    }

    suspend fun getOrderDetails(lastOid: Int): OrderDetails {
        return communicationController.getOrderDetails(lastOid);
    }

    suspend fun getMenuImage(mid: Int, imageVersion: Int):String{
        //Check if the (current version of the) image exists
        val result: MenuInfo? = menuDao.getMenuImage(mid, imageVersion);
        if(result==null){   //If the (current version of the) image does not exist
            Log.d(TAG,"Menu $mid's image doesn't exist in the local DB.");
            val image=(communicationController.getMenuImage(mid)).base64;
            val newMenu= MenuInfo(mid,imageVersion,image);
            menuDao.insertMenu(newMenu);
            return image;
        }
        //If the (current version of the) image exists
        Log.d(TAG,"Menu $mid's image exists in the local DB");
        return result.image;

    }

}