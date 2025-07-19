package com.example.mangiabastakotlin.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.mangiabastakotlin.InitialLoadingScreenTheme

@Composable
fun InitialLoadingScreen(){
    val theme=InitialLoadingScreenTheme();
    Column(
        modifier= theme.columnModifier,
        verticalArrangement = theme.verticalArrangement,
        horizontalAlignment = theme.horizontalAlignment
    ){
        Column(horizontalAlignment = theme.horizontalAlignment, verticalArrangement = theme.innerVerticalArrangement){
            Text(text="Mangia&Basta", style=theme.logoTextStyle);
            CircularProgressIndicator(color= Color.White)
        }

    }
}