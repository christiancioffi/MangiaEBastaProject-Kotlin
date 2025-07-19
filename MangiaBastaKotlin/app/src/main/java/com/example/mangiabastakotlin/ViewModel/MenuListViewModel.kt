package com.example.mangiabastakotlin.ViewModel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiabastakotlin.Exceptions.LocationNotAvailableException
import com.example.mangiabastakotlin.Exceptions.NetworkErrorException
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.LocationController
import com.example.mangiabastakotlin.Model.ImageState
import com.example.mangiabastakotlin.Model.MenuListState
import com.example.mangiabastakotlin.Model.Repositories.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(ExperimentalEncodingApi::class)
class MenuListViewModel(database: AppDatabase, private val locationController: LocationController): ViewModel(){
    private val TAG = MenuListViewModel::class.simpleName;
    private val menuRepository: MenuRepository=MenuRepository(database);
    private val _menuListState = MutableStateFlow<MenuListState>(MenuListState());
    val menuListState:StateFlow<MenuListState> = _menuListState;
    private val _menuImagesStates: MutableList<MutableStateFlow<ImageState>> = mutableListOf<MutableStateFlow<ImageState>>()
    val menuImagesStates: MutableList<StateFlow<ImageState>> = mutableListOf<StateFlow<ImageState>>()

    init{
        Log.d(TAG,"ViewModel created!");
        getMenuInTheNeighbourhood();
    }

    fun retry(){
        _menuListState.value=MenuListState();
        getMenuInTheNeighbourhood();
    }

    private fun getMenuInTheNeighbourhood(){
        viewModelScope.launch{
            try{
                val location=locationController.getLocation();
                val menuList=menuRepository.getMenuByLocation(location)
                if(menuList.isNotEmpty()){
                    menuList.forEachIndexed{ index,menu ->
                        _menuImagesStates.add(MutableStateFlow<ImageState>(ImageState()));
                        menuImagesStates.add(_menuImagesStates[index]);
                    }
                    _menuListState.value= MenuListState(loadingState = "Loaded", menuList=menuList);
                    loadMenuImages();
                }else{
                    _menuListState.value= MenuListState(loadingState = "Error", errorMessage="No menus are available near you");
                }
            }catch(nee: NetworkErrorException){
                Log.d(TAG, nee.message)
                _menuListState.value= MenuListState(loadingState = "Error", errorMessage=nee.message);
            }catch (le: LocationNotAvailableException){
                Log.d(TAG, le.message);
                _menuListState.value= MenuListState(loadingState = "Error", errorMessage="Something went wrong");
            }catch (ce: CancellationException){
                Log.d(TAG, "Job cancelled");
                throw CancellationException();
            } catch(e: Exception){
                Log.d(TAG, "Error while retrieving the menu list",e);
                Log.d(TAG, "Exception Type: ${e::class.simpleName}");
                Log.d(TAG, "Exception message: ${e.message ?: "Error"}");
                _menuListState.value= MenuListState(loadingState = "Error", errorMessage="Something went wrong");
            }
        }
    }

    private fun loadMenuImages(){
        _menuListState.value.menuList.forEachIndexed { index,menu ->
            viewModelScope.launch{
                var imageState: ImageState?=null;
                try {
                    val base64Image=menuRepository.getMenuImage(
                        mid = menu.mid,
                        imageVersion = menu.imageVersion
                    );
                    val imageBitmap: ImageBitmap = getBitmapFromBase64(base64Image);
                    imageState= ImageState(loadingState="Loaded",image=imageBitmap)
                } catch(nee: NetworkErrorException){
                    Log.d(TAG,"Error while retrieving menu ${menu.mid}'s image: ${nee.message}")
                    imageState= ImageState(loadingState="Error")
                } catch (ce: CancellationException){
                    Log.d(TAG,"Job cancelled");
                    throw CancellationException();
                } catch (e: Exception) {
                    Log.d(TAG,"Error while retrieving menu ${menu.mid}'s image.",e)
                    Log.d(TAG, "Exception Type: ${e::class.simpleName}");
                    Log.d(TAG, "Exception message: ${e.message ?: "Error"}");
                    imageState= ImageState(loadingState="Error");
                }
                if(imageState!=null){
                    _menuImagesStates[index].value=imageState;
                }
            }
        }
    }

    private fun getBitmapFromBase64(base64Image: String):ImageBitmap{
        //delay(5000);
        val byteArray = Base64.decode(base64Image);
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return bitmap.asImageBitmap();
    }

    //For debugging
    override fun onCleared() {
        super.onCleared();
        Log.d(TAG, "ViewModel destroyed.")
    }

}