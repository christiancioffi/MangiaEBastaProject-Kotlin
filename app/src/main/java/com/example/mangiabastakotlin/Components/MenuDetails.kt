package com.example.mangiabastakotlin.Components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.mangiabastakotlin.MenuDetailsTheme
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.LocationController
import com.example.mangiabastakotlin.ViewModel.MenuDetailsViewModel
import com.google.android.gms.location.LocationServices
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDetails(navController: NavHostController, mid: Int){

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
            MenuDetailsViewModel(mid, database, locationController);
        }
    }
    val viewModel: MenuDetailsViewModel=viewModel(factory=factory);
    val detailedMenuState=viewModel.menuState.collectAsState();
    val appRequest=viewModel.appRequest.collectAsState();

    Column{
        NavigationBar(
            title="Menu Details",
            onClickLeftButton={
                if(!navController.popBackStack()){
                    navController.navigate("MenuList")
                }
            },
            leftButtonText = "Back",
        )
        when(detailedMenuState.value.loadingState){
            "Loading" -> LoadingScreen();
            "Error" -> ErrorScreen(detailedMenuState.value.errorMessage,viewModel::retry)
            "Loaded" -> {
                val menu=detailedMenuState.value.detailedMenu;
                val theme= MenuDetailsTheme();
                Column(modifier=theme.columnModifier.then(Modifier.verticalScroll(rememberScrollState())), horizontalAlignment = theme.horizontalAlignment){
                    Spacer(modifier= theme.separatorModifier)
                    Text(text=menu.name, modifier=theme.titleModifier, style=theme.titleStyle);
                    Spacer(modifier= theme.separatorModifier)
                    MenuImage(modifier=theme.imageModifierContainer, state=viewModel.menuImageState, );
                    Column(modifier=theme.menuInfoColumnModifier, verticalArrangement = theme.verticalArrangement){
                        val menuPriceText= buildAnnotatedString {
                            withStyle(style=theme.textLabelStyle){
                                append("Price: ")
                            }
                            val formattedPrice = NumberFormat.getCurrencyInstance(Locale("it", "IT")).format(menu.price)
                            append(formattedPrice)
                        }
                        Text(text=menuPriceText, softWrap=true)
                        val menuShortDescriptionText= buildAnnotatedString {
                            withStyle(style=theme.textLabelStyle){
                                append("Short description: ")
                            }
                            append(menu.shortDescription)
                        }
                        Text(text=menuShortDescriptionText, softWrap=true)
                        val menuLongDescriptionText= buildAnnotatedString {
                            withStyle(style=theme.textLabelStyle){
                                append("Long description: ")
                            }
                            append(menu.longDescription)
                        }
                        Text(text=menuLongDescriptionText, softWrap=true)
                        val menuExpectedDeliveryTimeText= buildAnnotatedString {
                            withStyle(style=theme.textLabelStyle){
                                append("Expected delivery time: ")
                            }
                            append("${menu.deliveryTime} minutes")
                        }
                        Text(text=menuExpectedDeliveryTimeText, softWrap=true)
                    }
                    ButtonWithLoadingIndicator(text="Order", onClick=viewModel::order, requestState = appRequest.value.requestState);
                    Spacer(modifier= theme.separatorModifier)
                    if(appRequest.value.requestState=="Received"){
                        val details=appRequest.value;
                        AlertMessage(
                            title=details.title,
                            message=details.message,
                            onConfirmation={
                                val oid=details.orderId;
                                viewModel.deleteAlert()
                                if(oid!=-1){
                                    navController.navigate("OrderTracking/$oid");
                                }
                            }
                        )
                    }
                }
            }
        }
    }

}