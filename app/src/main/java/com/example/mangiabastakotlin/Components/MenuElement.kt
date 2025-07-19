package com.example.mangiabastakotlin.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.mangiabastakotlin.MenuElementTheme
import com.example.mangiabastakotlin.Model.ImageState
import com.example.mangiabastakotlin.Model.MenuShortDetails
import kotlinx.coroutines.flow.StateFlow
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MenuElement(menu: MenuShortDetails, showDetails: (Int)->Unit, imageState: StateFlow<ImageState>){

    val theme=MenuElementTheme();
    Column(modifier=Modifier.clickable{showDetails(menu.mid)}.then(theme.columnModifier)){
        MenuImage(modifier=theme.imageModifierContainer, state=imageState);
        Column(modifier=theme.menuInfoColumnModifier, verticalArrangement = theme.verticalArrangement){
            val menuNameText= buildAnnotatedString {
                withStyle(style=theme.textLabelStyle){
                    append("Menu name: ")
                }
                append(menu.name)
            }
            Text(text=menuNameText, softWrap=true)
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
            val menuExpectedDeliveryTimeText= buildAnnotatedString {
                withStyle(style=theme.textLabelStyle){
                    append("Expected delivery time: ")
                }
                append("${menu.deliveryTime} minutes")
            }
            Text(text=menuExpectedDeliveryTimeText, softWrap=true)
        }
    }
}
