package com.example.mangiabastakotlin.ViewModel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiabastakotlin.Exceptions.IncompleteProfileException
import com.example.mangiabastakotlin.Exceptions.InvalidCardException
import com.example.mangiabastakotlin.Exceptions.LocationNotAvailableException
import com.example.mangiabastakotlin.Exceptions.NetworkErrorException
import com.example.mangiabastakotlin.Exceptions.NotFoundException
import com.example.mangiabastakotlin.Exceptions.OngoingOrderException
import com.example.mangiabastakotlin.Model.AppRequest
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.LocationController
import com.example.mangiabastakotlin.Model.DetailedMenuState
import com.example.mangiabastakotlin.Model.ImageState
import com.example.mangiabastakotlin.Model.Repositories.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class MenuDetailsViewModel(private val mid: Int, database: AppDatabase, private val locationController: LocationController): ViewModel() {
    private val TAG = MenuDetailsViewModel::class.simpleName;
    private val menuRepository: MenuRepository = MenuRepository(database);
    private val _menuState= MutableStateFlow<DetailedMenuState>(DetailedMenuState())
    val menuState: StateFlow<DetailedMenuState> = _menuState;
    private val _menuImageState= MutableStateFlow<ImageState>(ImageState())
    val menuImageState: StateFlow<ImageState> = _menuImageState;
    private val _appRequest=MutableStateFlow<AppRequest>(AppRequest());
    val appRequest:StateFlow<AppRequest> = _appRequest;

    init{
        Log.d(TAG,"ViewModel created!");
        getMenuDetails(mid);
    }

    fun retry(){
        _menuState.value=DetailedMenuState();
        getMenuDetails(mid);
    }

    private fun getMenuDetails(mid:Int){
        viewModelScope.launch{
            try{
                val location=locationController.getLocation();
                val detailedMenu=menuRepository.getMenuDetails(mid, location);
                _menuState.value= DetailedMenuState(loadingState="Loaded", detailedMenu=detailedMenu);
                loadMenuImage();
            }catch(nee: NetworkErrorException){
                Log.d(TAG, nee.message)
                _menuState.value= DetailedMenuState(loadingState="Error", errorMessage=nee.message);
            }catch(nfe: NotFoundException){
                Log.d(TAG, nfe.message);
                _menuState.value= DetailedMenuState(loadingState="Error", errorMessage="Menu not found");
            }catch (ce: CancellationException){
                Log.d(TAG, "Job cancelled");
                throw CancellationException();
            }catch(le: LocationNotAvailableException){
                Log.d(TAG, le.message)
                _menuState.value= DetailedMenuState(loadingState="Error", errorMessage="Something went wrong");
            }catch(e: Exception){
                Log.d(TAG, "Error while retrieving menu $mid's details.")
                _menuState.value= DetailedMenuState(loadingState="Error", errorMessage="Something went wrong");
            }
        }
    }

    private suspend fun loadMenuImage(){
        try {
            val base64Image=menuRepository.getMenuImage(
                mid = _menuState.value.detailedMenu.mid,
                imageVersion = _menuState.value.detailedMenu.imageVersion
            );
            val menuImage=getBitmapFromBase64(base64Image);
            _menuImageState.value = ImageState(loadingState="Loaded",image=menuImage);
        } catch(nee: NetworkErrorException){
            Log.d(TAG, nee.message);
            _menuImageState.value = ImageState(loadingState="Error");
        } catch (ce: CancellationException){
            Log.d(TAG, "Job cancelled");
            throw CancellationException();
        }catch (e: Exception) {
            Log.d(TAG, e.message ?: "Error");
            _menuImageState.value = ImageState(loadingState="Error");
        }
    }

    private fun getBitmapFromBase64(base64Image: String):ImageBitmap{
        //delay(5000);
        val byteArray = Base64.decode(base64Image);
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return bitmap.asImageBitmap();
    }

    fun order(){
        viewModelScope.launch{
            _appRequest.value=AppRequest(requestState="Waiting");
            val mid=menuState.value.detailedMenu.mid;
            try{
                val location=locationController.getLocation();
                Log.d(TAG, "Ordering menu $mid...");
                val orderDetails=menuRepository.orderMenu(mid, location);
                Log.d(TAG,"$orderDetails");
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Success",
                    message="Your order has been executed. Food is coming your way.",
                    orderId=orderDetails.oid
                )
            }catch(ipe: IncompleteProfileException){
                Log.d(TAG,"$${ipe.message}. Wrong field is ${ipe.wrongField}");
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Error",
                    message=ipe.message
                )
            }catch(ooe: OngoingOrderException){
                Log.d(TAG,ooe.message);
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Error",
                    message=ooe.message
                )
            }catch(nee: NetworkErrorException){
                Log.d(TAG,nee.message);
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Error",
                    message=nee.message
                )
            }catch(ice: InvalidCardException){
                Log.d(TAG,ice.message);
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Error",
                    message=ice.message
                )
            }catch (ce: CancellationException){
                Log.d(TAG, "Job cancelled");
                throw CancellationException();
            }catch(le: LocationNotAvailableException){
                Log.d(TAG, le.message)
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Error",
                    message="Something went wrong"
                )
            }catch(e: Exception){
                val message=e.message ?: "Something went wrong"
                Log.d(TAG,"Impossible to order menu $mid. Error: $message")
                _appRequest.value=AppRequest(
                    requestState="Received",
                    title="Error",
                    message=message
                )
            }
        }
    }

    fun deleteAlert(){
        _appRequest.value=AppRequest();
    }

    //For debugging
    override fun onCleared() {
        super.onCleared();
        Log.d(TAG, "ViewModel destroyed.")
    }

}