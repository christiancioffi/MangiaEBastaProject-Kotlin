package com.example.mangiabastakotlin.ViewModel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiabastakotlin.Exceptions.LocationNotAvailableException
import com.example.mangiabastakotlin.Exceptions.NetworkErrorException
import com.example.mangiabastakotlin.Exceptions.NotFoundException
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.LocationController
import com.example.mangiabastakotlin.Model.OrderDetails
import com.example.mangiabastakotlin.Model.Repositories.MenuRepository
import com.example.mangiabastakotlin.Model.TrackedOrderState
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.annotation.generated.withTextHaloColor
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class OrderTrackingViewModel(private val oid: Int, database: AppDatabase, private val locationController: LocationController): ViewModel() {
    private val TAG=OrderTrackingViewModel::class.simpleName;
    private val menuRepository= MenuRepository(database);
    private val _trackedOrderState=MutableStateFlow<TrackedOrderState>(TrackedOrderState());
    val trackedOrderState:StateFlow<TrackedOrderState> =_trackedOrderState;
    private val droneUpdateInterval:Long=5000;

    init{
        Log.d(TAG,"ViewModel created!")
        trackOrder(oid);
    }

    fun retry(){
        _trackedOrderState.value=TrackedOrderState();
        trackOrder(oid);
    }

    private fun trackOrder(oid: Int){
        viewModelScope.launch{
            try {
                val orderDetails=menuRepository.getOrderDetails(oid);
                val location=locationController.getLocation();
                val menuDetails=menuRepository.getMenuDetails(orderDetails.mid,location);
                _trackedOrderState.value= TrackedOrderState(loadingState="Loaded",orderDetails=orderDetails, orderedMenu=menuDetails);
            }catch(nee: NetworkErrorException){
                Log.d(TAG, nee.message)
                _trackedOrderState.value= TrackedOrderState(loadingState="Error", errorMessage=nee.message);
            }catch(nfe: NotFoundException){
                Log.d(TAG, nfe.message);
                _trackedOrderState.value= TrackedOrderState(loadingState="Error", errorMessage="Order not found");
            }catch(le: LocationNotAvailableException){
                Log.d(TAG, le.message)
                _trackedOrderState.value= TrackedOrderState(loadingState="Error", errorMessage="Something went wrong");
            }catch (ce: CancellationException){
                Log.d(TAG, "Job cancelled");
                throw CancellationException();
            }catch(e: Exception){
                Log.d(TAG, "Error while retrieving order details for tracking",e)
                _trackedOrderState.value= TrackedOrderState(loadingState="Error", errorMessage="Something went wrong");
            }
        }
    }

    suspend fun setAnnotationLocations(pointAnnotationManager: PointAnnotationManager, restaurantIcon: Bitmap, droneIcon: Bitmap, deliveryIcon: Bitmap){
        try {
            var order=_trackedOrderState.value.orderDetails;
            val menu=_trackedOrderState.value.orderedMenu;
            if(order.status=="COMPLETED"){

                pointAnnotationManager.create(
                    PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(order.deliveryLocation.lng, order.deliveryLocation.lat))
                        .withIconImage(deliveryIcon)
                        .withTextField("Delivery location")
                        .withTextHaloColor(Color.White)
                        .withTextHaloWidth(2.0)
                        .withTextHaloBlur(1.0),
                )
            }else if(order.status=="ON_DELIVERY"){

                pointAnnotationManager.create(
                    PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(menu.location.lng, menu.location.lat))
                        .withIconImage(restaurantIcon)
                        .withTextField("Restaurant location")
                        .withTextHaloColor(Color.White)
                        .withTextHaloWidth(2.0)
                        .withTextHaloBlur(1.0),
                )

                pointAnnotationManager.create(
                    PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(order.deliveryLocation.lng, order.deliveryLocation.lat))
                        .withIconImage(deliveryIcon)
                        .withTextField("Delivery location")
                        .withTextHaloColor(Color.White)
                        .withTextHaloWidth(2.0)
                        .withTextHaloBlur(1.0),
                )

                var dronePointAnnotation:PointAnnotation?=null;
                Log.d(TAG, "Started watching drone current location....");
                while(order.status!="COMPLETED"){
                    order=menuRepository.getOrderDetails(_trackedOrderState.value.orderDetails.oid);
                    Log.d(TAG,"New details received");
                    dronePointAnnotation?.let{pointAnnotationManager.delete(it)}
                    dronePointAnnotation=pointAnnotationManager.create(
                        PointAnnotationOptions()
                            .withPoint(Point.fromLngLat(order.currentPosition.lng, order.currentPosition.lat))
                            .withIconImage(droneIcon)
                            .withTextField("Drone current location")
                            .withTextHaloColor(Color.White)
                            .withTextHaloWidth(2.0)
                            .withTextHaloBlur(1.0),
                    )
                    delay(droneUpdateInterval);
                }
                pointAnnotationManager.deleteAll();
                pointAnnotationManager.create(
                    PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(order.deliveryLocation.lng, order.deliveryLocation.lat))
                        .withIconImage(deliveryIcon)
                        .withTextField("Delivery location")
                        .withTextHaloColor(Color.White)
                        .withTextHaloWidth(2.0)
                        .withTextHaloBlur(1.0),
                )
                orderCompleted(order);
            }
        }catch (ce: CancellationException){
            Log.d(TAG, "Job cancelled");
            throw CancellationException();
        }catch(nee: NetworkErrorException){
            Log.d(TAG, nee.message)
        }catch(e: Exception){
            Log.d(TAG, "Error while retrieving drone location",e);
        }
    }

    private fun orderCompleted(orderDetails: OrderDetails){
        Log.d(TAG,"Order completed");
        _trackedOrderState.value=
            TrackedOrderState(loadingState="Loaded",orderDetails=orderDetails, orderedMenu=_trackedOrderState.value.orderedMenu);
    }

    //For debugging
    override fun onCleared() {
        super.onCleared();
        Log.d(TAG, "ViewModel destroyed.")
    }

}