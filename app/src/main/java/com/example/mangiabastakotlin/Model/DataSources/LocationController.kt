package com.example.mangiabastakotlin.Model.DataSources

import android.os.Looper
import android.util.Log
import com.example.mangiabastakotlin.Exceptions.LocationNotAvailableException
import com.example.mangiabastakotlin.Model.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class LocationController(private val fusedLocationClient: FusedLocationProviderClient) {

    private val TAG=LocationController::class.simpleName;
    private var locationCallback: LocationCallback=object : LocationCallback(){};

    suspend fun getLocation(): Location {
        var message="";
        try{
            val task = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token);
            val locationObj=task.await();
            val location = Location(locationObj.latitude,locationObj.longitude);
            return location;
        }catch(se: SecurityException){      //Importante perchè altrimenti getCurrentLocation dà problemi.
            message="Permission Error: ${se.message}";
        }
        throw LocationNotAvailableException(message);
    }

    fun startWatchingPosition(callback:(location: Location?)->Unit, interval: Long = 5000){
        try{
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
                .build()

            locationCallback = object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult){
                    locationResult.locations.forEach{ locationObj ->
                        if(locationObj!=null){
                            val location=Location(locationObj.latitude,locationObj.longitude);
                            callback(location);
                        }else{
                            callback(null);
                        }
                    }
                }

            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }catch(se: SecurityException){      //Importante perchè altrimenti getCurrentLocation dà problemi.
            Log.d(TAG,"Permission Error: ${se.message}");
        }
    }

    fun stopWatchingPosition(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    suspend fun getLastLocation():Location?{
        var message="";
        try{
            val task=fusedLocationClient.getLastLocation();
            val locationObj=task.await();
            if(locationObj!=null){
                val location=Location(locationObj.latitude,locationObj.longitude);
                return location;
            }
            return null;
        }catch(se: SecurityException){      //Importante perchè altrimenti getCurrentLocation dà problemi.
            message="Permission Error: ${se.message}";
        }
        throw LocationNotAvailableException(message);
    }
}