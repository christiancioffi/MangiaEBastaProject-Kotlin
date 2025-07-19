package com.example.mangiabastakotlin.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.room.Room
import com.example.mangiabastakotlin.Model.DataSources.AppDatabase
import com.example.mangiabastakotlin.Model.DataSources.LocationController
import com.example.mangiabastakotlin.OrderTrackingTheme
import com.example.mangiabastakotlin.ViewModel.OrderTrackingViewModel
import com.google.android.gms.location.LocationServices
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun OrderTracking(navController: NavController, oid: Int){

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
            OrderTrackingViewModel(oid,database,locationController);
        }
    }
    val viewModel: OrderTrackingViewModel=viewModel(factory=factory);
    val trackedOrderState=viewModel.trackedOrderState.collectAsState();
    val theme= OrderTrackingTheme();
    val menu=trackedOrderState.value.orderedMenu;
    val order=trackedOrderState.value.orderDetails;

    Column{
        NavigationBar(
            title="Order Tracking",
            onClickLeftButton={
                if(!navController.popBackStack()){
                    navController.navigate("MenuList")
                }
            },
            leftButtonText = "Back",
        )
        when(trackedOrderState.value.loadingState){
            "Loading" -> LoadingScreen();
            "Error" -> ErrorScreen(trackedOrderState.value.errorMessage, viewModel::retry)
            "Loaded" -> {   //.then(Modifier.verticalScroll(rememberScrollState()))
                Column(modifier=theme.columnModifier, horizontalAlignment = theme.horizontalAlignment){
                    Spacer(modifier= theme.separatorModifier)
                    Text(text="Order #${order.oid}", modifier=theme.titleModifier, style=theme.titleStyle);
                    Spacer(modifier= theme.separatorModifier)
                    OrderTrackingMap(viewModel::setAnnotationLocations);
                    Spacer(modifier= theme.separatorModifier);
                    Column(modifier=theme.orderInfoColumnModifier, verticalArrangement = theme.verticalArrangement){
                        val menuNameText= buildAnnotatedString {
                            withStyle(style=theme.textLabelStyle){
                                append("Menu name: ")
                            }
                            append(menu.name)
                        }
                        Text(text=menuNameText, softWrap=true)

                        val orderStatusText= buildAnnotatedString {
                            withStyle(style=theme.textLabelStyle){
                                append("Order status: ")
                            }
                            if(order.status=="ON_DELIVERY"){
                                withStyle(style=theme.onDeliveryTextStyle){
                                    append("On delivery...")
                                }
                            }else if(order.status=="COMPLETED"){
                                withStyle(style=theme.completedTextStyle){
                                    append("Completed")
                                }
                            }else{
                                append("Unknown")
                            }
                        }
                        Text(text=orderStatusText, softWrap=true)

                        if(order.status=="ON_DELIVERY"){
                            var instant = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(order.expectedDeliveryTimestamp))
                            val expectedDeliveryTimestamp:Long=instant.toEpochMilli()
                            instant = Instant.ofEpochMilli(expectedDeliveryTimestamp)
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm").withZone(
                                ZoneId.of("Europe/Rome"))
                            val formattedDateTime=formatter.format(instant)
                            val orderDeliveryTimeText= buildAnnotatedString {
                                withStyle(style=theme.textLabelStyle){
                                    append("Expected delivery on ")
                                }
                                append(formattedDateTime)
                            }
                            Text(text=orderDeliveryTimeText, softWrap=true)
                            Timer(expectedDeliveryTimestamp)
                        }else if(order.status=="COMPLETED"){
                            val orderDeliveryTimeText= buildAnnotatedString {
                                withStyle(style=theme.textLabelStyle){
                                    append("Delivery on ")
                                }
                                var instant = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(order.deliveryTimestamp))
                                val deliveryTimestamp:Long=instant.toEpochMilli()
                                instant = Instant.ofEpochMilli(deliveryTimestamp)
                                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm").withZone(ZoneId.of("Europe/Rome"))
                                val formattedDateTime=formatter.format(instant)
                                append(formattedDateTime)
                            }
                            Text(text=orderDeliveryTimeText, softWrap=true)
                        }

                    }
                }

            }
        }
    }
}