package com.example.mangiabastakotlin.Components

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.room.Room
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.LocationController
import com.example.mangiabastakotlin.ProfileTheme
import com.example.mangiabastakotlin.ViewModel.ProfileViewModel
import com.google.android.gms.location.LocationServices


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(navController: NavController){
    val context = LocalContext.current;
    val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "appDB"
    ).build();
    val fusedLocationClient = remember{ LocationServices.getFusedLocationProviderClient(context) }
    val locationController= LocationController(fusedLocationClient);
    val factory = viewModelFactory {
        initializer {
            ProfileViewModel(database, locationController);
        }
    }
    val viewModel: ProfileViewModel=viewModel(factory=factory);
    val profileState=viewModel.profileState.collectAsState();
    val appRequest=viewModel.appRequest.collectAsState();

    Column{
        NavigationBar(
            title="Profile",
            onClickLeftButton={
                if(!navController.popBackStack()){
                    navController.navigate("MenuList")
                }
            },
            leftButtonText = "Back",
        )
        when(profileState.value.loadingState){
            "Loading" -> LoadingScreen();
            "Error" -> ErrorScreen(profileState.value.errorMessage, viewModel::retry)
            else -> {
                val theme= ProfileTheme();
                Column(modifier=theme.columnModifier.then(
                    Modifier.verticalScroll(
                        rememberScrollState()
                    )), horizontalAlignment = theme.horizontalAlignment){
                    Spacer(modifier= theme.separatorModifier)
                    Text(text="Personal Information", modifier=theme.titleModifier, style=theme.titleStyle);
                    Spacer(modifier= theme.separatorModifier)
                    ProfileElement(
                        fieldValue=viewModel.getFirstName(),
                        label="First Name",
                        placeholder=viewModel.getFirstNamePlaceholder(),
                        sensible=false,
                        onValueChange={newValue -> viewModel.setFirstName(newValue) },
                        maximumLength=15
                    );
                    Spacer(modifier= theme.separatorModifier)
                    ProfileElement(
                        fieldValue=viewModel.getLastName(),
                        label="Last Name",
                        placeholder=viewModel.getLastNamePlaceholder(),
                        sensible=false,
                        onValueChange={newValue -> viewModel.setLastName(newValue) },
                        maximumLength=15
                    );
                    Spacer(modifier= theme.separatorModifier)
                    ProfileElement(
                        fieldValue=viewModel.getCardFullName(),
                        label="Card Full Name",
                        placeholder=viewModel.getCardFullNamePlaceholder(),
                        sensible=false,
                        onValueChange={newValue -> viewModel.setCardFullName(newValue) },
                        maximumLength=31
                    );
                    Spacer(modifier= theme.separatorModifier)
                    ProfileElement(
                        fieldValue=viewModel.getCardNumber(),
                        label="Card number",
                        placeholder=viewModel.getCardNumberPlaceholder(),
                        sensible=true,
                        onValueChange={newValue -> viewModel.setCardNumber(newValue) },
                        maximumLength=16
                    );
                    Spacer(modifier= theme.separatorModifier)
                    ProfileElement(
                        fieldValue=viewModel.getCardExpireMonth(),
                        label="Card expire month",
                        placeholder=viewModel.getCardExpireMonthPlaceholder(),
                        sensible=false,
                        onValueChange={newValue -> viewModel.setCardExpireMonth(newValue)},
                        maximumLength=9
                    );
                    Spacer(modifier= theme.separatorModifier)
                    ProfileElement(
                        fieldValue=viewModel.getCardExpireYear(),
                        label="Card expire year",
                        placeholder=viewModel.getCardExpireYearPlaceholder(),
                        sensible=false,
                        onValueChange={newValue -> viewModel.setCardExpireYear(newValue);},
                        maximumLength=4
                    );
                    Spacer(modifier= theme.separatorModifier)
                    ProfileElement(
                        fieldValue=viewModel.getCardCVV(),
                        label="Card CVV",
                        placeholder=viewModel.getCardCVVPlaceholder(),
                        sensible=true,
                        onValueChange={newValue -> viewModel.setCardCVV(newValue); },
                        maximumLength=3
                    );
                    Spacer(modifier= theme.separatorModifier)
                    ButtonWithLoadingIndicator(text="Send", onClick=viewModel::sendUserDetails, requestState = appRequest.value.requestState);
                    Spacer(modifier= theme.separatorModifier)
                    LastOrderElement(
                        viewModel.lastOrderState,
                        viewModel.lastOrderImageState,
                        {
                            val oid=viewModel.lastOrderState.value.lastOrder.oid;
                            navController.navigate("OrderTracking/$oid");
                        }
                    )
                    Spacer(modifier= theme.separatorModifier)
                    if(appRequest.value.requestState=="Received"){
                        val details=appRequest.value;
                        AlertMessage(
                            title=details.title,
                            message=details.message,
                            onConfirmation=viewModel::deleteAlert
                        )
                    }
                }
            }
        }
    }
}