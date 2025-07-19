package com.example.mangiabastakotlin.Components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.mangiabastakotlin.ButtonWithLoadingIndicatorTheme

@Composable
fun ButtonWithLoadingIndicator(text: String="Click", onClick: () -> Unit, requestState : String){

    val buttonTheme= ButtonWithLoadingIndicatorTheme();
    Button(onClick={
        onClick();
    },colors= ButtonDefaults.buttonColors(containerColor = buttonTheme.color)){
        Row (verticalAlignment = buttonTheme.rowAlignment, horizontalArrangement = buttonTheme.horizontalArrangement){
            if(requestState=="Waiting"){
                CircularProgressIndicator(color= Color.White, modifier=buttonTheme.indicatorModifier)
            }
            Text(text, style=buttonTheme.textStyle)
        }
    }
}