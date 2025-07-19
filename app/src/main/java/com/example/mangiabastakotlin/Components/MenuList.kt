package com.example.mangiabastakotlin.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.mangiabastakotlin.MenuListTheme
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.LocationController
import com.example.mangiabastakotlin.Model.Repositories.MenuRepository
import com.example.mangiabastakotlin.ViewModel.MenuListViewModel
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuList(navController: NavHostController){
    val context = LocalContext.current;
    val fusedLocationClient = remember{ LocationServices.getFusedLocationProviderClient(context) }
    val locationController= LocationController(fusedLocationClient);
    val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "appDB"
    ).build();
    val factory = viewModelFactory {
        initializer {
            MenuListViewModel(database, locationController);
        }
    }

    val viewModel: MenuListViewModel=viewModel(factory=factory);       //Automaticamente associato al NavHost???

    val menuListState = viewModel.menuListState.collectAsState();

    Column{
        NavigationBar(
            title="Menu List",
            onClickRightButton={
                navController.navigate("Profile");
            },
            rightButtonText = "Profile",
        )
        when(menuListState.value.loadingState){
            "Loading" -> LoadingScreen();
            "Error" -> ErrorScreen(menuListState.value.errorMessage, viewModel::retry)
            "Loaded" -> {
                val menuList=menuListState.value.menuList;
                val theme= MenuListTheme();
                LazyColumn(modifier=theme.lazyColumnModifier, horizontalAlignment = theme.horizontalAlignment){
                    item{
                        Spacer(modifier= theme.separatorModifier)
                        Text("Available menus", modifier= theme.titleModifier, style=theme.titleStyle)
                        Spacer(modifier= theme.separatorModifier)
                    }
                    val menuImagesStates=viewModel.menuImagesStates;
                    itemsIndexed(menuList){ index, menu ->
                        val menuImageState=menuImagesStates[index];
                        MenuElement(
                            menu,
                            { mid: Int ->
                                navController.navigate("MenuDetails/$mid")
                            },
                            menuImageState,
                        )
                        Spacer(modifier= theme.separatorModifier)
                    }
                }
            }
        }
    }


}

