package com.example.mangiabastakotlin

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.mangiabastakotlin.Components.ErrorScreen
import com.example.mangiabastakotlin.Components.InitialLoadingScreen
import com.example.mangiabastakotlin.Components.Screen
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.DataStoreManager
import com.example.mangiabastakotlin.Model.Repositories.ScreenRepository
import com.example.mangiabastakotlin.ViewModel.AppViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "appSettings")

class MainActivity : ComponentActivity() {
    private val TAG=MainActivity::class.simpleName;

    private var generalAppViewModel: AppViewModel? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val context: Context=this;
        val database = Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "appDB"
        ).build();
        val dataStoreManager= DataStoreManager(dataStore);
        val factory = viewModelFactory {
            initializer {
                AppViewModel(database, dataStoreManager, context);
            }
        }
        //appViewModel = ViewModelProvider(this, factory).get(AppViewModel::class.java);
        val appViewModel: AppViewModel by viewModels(){factory};
        generalAppViewModel=appViewModel;
        setContent {
            MyApp(appViewModel);
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"App in background");
        generalAppViewModel?.saveCurrentScreenState();
    }
}

@Composable
fun MyApp(viewModel: AppViewModel) {

    val appState=viewModel.appState.collectAsState();
    val context = LocalContext.current;
    val navController = rememberNavController();
    val launchPermission=viewModel.launchPermission.collectAsState();
    val listener={ _: NavController, _: NavDestination, _: Bundle? ->
        val currentEntry=navController.currentBackStackEntry;
        Log.d(AppViewModel::class.simpleName,"Navigation Stack: ${navController.backQueue.map{it.destination.route}}")
        if(currentEntry!=null){
            viewModel.setCurrentScreenState(currentEntry);
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if(isGranted){
            viewModel.permissionGranted();
        }else{
            viewModel.permissionNotGranted();
        }
    }

    LaunchedEffect(navController){
        navController.removeOnDestinationChangedListener(listener);
        navController.addOnDestinationChangedListener(listener);
    }

    LaunchedEffect(launchPermission.value){
        if(launchPermission.value){
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    when(appState.value.loadingState){
        "Loading"->InitialLoadingScreen();
        "Error"-> ErrorScreen(errorMessage=appState.value.errorMessage, retry={viewModel.retry(context)});
        else -> Screen(navController, appState.value.screenStateToBeRestored);
    }

}
