package com.example.mangiabastakotlin.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.mangiabastakotlin.LoadingScreenTheme
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(){

    var timeout by rememberSaveable{ mutableStateOf(false) };

    LaunchedEffect(Unit) {
        delay(10000)
        if(!timeout){
            timeout=true;
        }
    }
    val theme= LoadingScreenTheme();
    Column(
        modifier= theme.columnModifier,
        verticalArrangement = theme.verticalArrangement,
        horizontalAlignment = theme.horizontalAlignment
    ){
        Column(horizontalAlignment = theme.horizontalAlignment, verticalArrangement = theme.innerVerticalArrangement){
            CircularProgressIndicator(color= Color.White)
            if(timeout){
                Text("Be sure to be connected to the network");
            }
        }
    }


}