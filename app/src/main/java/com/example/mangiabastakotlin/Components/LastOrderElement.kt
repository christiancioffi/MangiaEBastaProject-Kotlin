package com.example.mangiabastakotlin.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.mangiabastakotlin.LastOrderElementTheme
import com.example.mangiabastakotlin.Model.ImageState
import com.example.mangiabastakotlin.Model.LastOrderState
import kotlinx.coroutines.flow.StateFlow
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LastOrderElement(state: StateFlow<LastOrderState>, lastOrderImageState: StateFlow<ImageState>, goToOrderTracking: ()->Unit){

    val lastOrderState=state.collectAsState()
    val theme = LastOrderElementTheme();

    Text(text="Last order", modifier=theme.titleModifier, style=theme.titleStyle);
    Spacer(modifier= theme.separatorModifier)

    when(lastOrderState.value.loadingState){
        "Loading" -> CircularProgressIndicator(color= Color.White);
        else ->{
            Column(modifier=Modifier.clickable{goToOrderTracking()}.then(theme.columnModifier)){
                when(lastOrderState.value.loadingState){
                    "Error" -> {
                        MenuImage(modifier=theme.imageModifierContainer)
                        Column(modifier=theme.menuInfoColumnModifier, horizontalAlignment = theme.horizontalAlignment){
                            Text(text=lastOrderState.value.errorMessage, style=theme.textErrorStyle);
                        }
                    }
                    else -> {
                        MenuImage(modifier=theme.imageModifierContainer,state=lastOrderImageState)
                        val lastOrder=lastOrderState.value.lastOrder;
                        val lastOrderedMenu=lastOrderState.value.lastOrderedMenu;
                        Column(modifier=theme.menuInfoColumnModifier, verticalArrangement = theme.verticalArrangement){
                            val menuNameText= buildAnnotatedString {
                                withStyle(style=theme.textLabelStyle){
                                    append("Menu name: ")
                                }
                                append(lastOrderedMenu.name)
                            }
                            Text(text=menuNameText, softWrap=true)
                            val menuPriceText= buildAnnotatedString {
                                withStyle(style=theme.textLabelStyle){
                                    append("Price: ")
                                }
                                val formattedPrice = NumberFormat.getCurrencyInstance(Locale("it", "IT")).format(lastOrderedMenu.price)
                                append(formattedPrice)
                            }
                            Text(text=menuPriceText, softWrap=true)
                            val orderStatusText= buildAnnotatedString {
                                withStyle(style=theme.textLabelStyle){
                                    append("Order status: ")
                                }
                                when(lastOrder.status){
                                    "COMPLETED" ->
                                        withStyle(style=theme.completedTextStyle){
                                            append("Completed")
                                        }
                                    "ON_DELIVERY" ->
                                        withStyle(style=theme.onDeliveryTextStyle){
                                            append("On delivery")
                                        }
                                    else -> append("Unknown")
                                }
                            }
                            Text(text=orderStatusText, softWrap=true)
                        }

                    }
                }
            }
        }
    }
}