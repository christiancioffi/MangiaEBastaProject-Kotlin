package com.example.mangiabastakotlin.ViewModel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import com.example.mangiabastakotlin.Exceptions.NetworkErrorException
import com.example.mangiabastakotlin.Model.AppState
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.DataStoreManager
import com.example.mangiabastakotlin.Model.Repositories.ScreenRepository
import com.example.mangiabastakotlin.Model.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class AppViewModel (database: AppDatabase, dsManager: DataStoreManager, context: Context): ViewModel() {
    private val TAG=AppViewModel::class.simpleName;
    private val screenRepository: ScreenRepository = ScreenRepository(database, dsManager);
    private val _appState= MutableStateFlow<AppState>(AppState());
    val appState: StateFlow<AppState> = _appState;
    private val _launchPermission=MutableStateFlow<Boolean>(false);
    val launchPermission:StateFlow<Boolean> = _launchPermission;
    private var currentScreenState=ScreenState();

    init{
        initializeApp(context);
    }

    fun retry(context: Context){
        _appState.value=AppState()          //Per ritornare allo stato di Loading
        initializeApp(context);
    }

    private fun initializeApp(context: Context){
        Log.d(TAG, "Check permissions...")
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
        if (hasPermission) {
            permissionGranted();
        } else {
            _launchPermission.value=true;
        }
    }

    fun permissionGranted(){
        _launchPermission.value=false;
        Log.d(TAG, "Permission granted!");
        viewModelScope.launch{
            try{
                screenRepository.configureSidUid();
                val screenStateToBeRestored=screenRepository.restoreLastScreenState();
                if(screenStateToBeRestored!=null){
                    Log.d(TAG, "Starting with $screenStateToBeRestored");
                    _appState.value= AppState(loadingState = "Loaded", screenStateToBeRestored = screenStateToBeRestored)
                }else{  //In caso di crash nessuna schermata viene salvata
                    Log.d(TAG, "No screen saved. Starting with default screen");
                    _appState.value= AppState(loadingState = "Loaded")
                }
            }catch(nee: NetworkErrorException){
                Log.d(TAG, nee.message);
                _appState.value= AppState(loadingState = "Error", errorMessage=nee.message)
            }catch(ce: CancellationException){
                Log.d(TAG, "Job cancelled");
                throw CancellationException();
            }catch(e: Exception){
                Log.d(TAG, e.message ?: "Error", e);
                _appState.value= AppState(loadingState = "Error", errorMessage="Something went wrong")
            }
        }
    }

    fun permissionNotGranted(){
        _launchPermission.value=false;
        Log.d(TAG, "Permission NOT granted.");
        _appState.value= AppState(loadingState = "Error", errorMessage="This app requires location permissions")
    }

    fun saveCurrentScreenState(){
        viewModelScope.launch{
            try{
                Log.d(TAG, "Saving current screen state $currentScreenState ...");
                screenRepository.saveCurrentScreenState(currentScreenState);
                Log.d(TAG, "Current screen state saved");
            }catch(ce: CancellationException){
                Log.d(TAG, "Job cancelled");
                throw CancellationException();
            }
            catch(e:Exception){
                Log.d(TAG,"An error occurred while saving current screen state $currentScreenState",e)
            }

        }
    }

    fun setCurrentScreenState(currentEntry: NavBackStackEntry){
        var currentScreenState: ScreenState? = null;
        val route = currentEntry.destination.route;
        val arguments = currentEntry.arguments;
        when(route){
            "MenuList"-> currentScreenState= ScreenState(route);
            "MenuDetails/{mid}" ->{
                val mid:Int = (arguments?.getString("mid") ?: "").toIntOrNull() ?: _appState.value.screenStateToBeRestored.detailedMid
                currentScreenState= ScreenState(route, detailedMid = mid);
            }
            "OrderTracking/{oid}" ->{
                val oid:Int = (arguments?.getString("oid") ?: "").toIntOrNull() ?: _appState.value.screenStateToBeRestored.trackedOid;
                currentScreenState= ScreenState(route, trackedOid = oid);
            }
            "Profile"->currentScreenState= ScreenState(route);
        }
        if(currentScreenState!=null){
            this.currentScreenState =currentScreenState;
        }else{
            Log.d(TAG,"Screen not recognized: $route");
        }
    }




}