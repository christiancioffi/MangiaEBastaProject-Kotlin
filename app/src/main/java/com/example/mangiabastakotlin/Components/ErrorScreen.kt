package com.example.mangiabastakotlin.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mangiabastakotlin.ErrorScreenTheme

@Composable
fun ErrorScreen(errorMessage: String, retry: ()->Unit){
    val theme=ErrorScreenTheme();

    Column(
        modifier= theme.columnModifier,
        verticalArrangement = theme.verticalArrangement,
        horizontalAlignment = theme.horizontalAlignment
    ){
        Column(horizontalAlignment = theme.horizontalAlignment, verticalArrangement = theme.innerVerticalArrangement){
            Text(text=errorMessage);
            Button(onClick={retry()}, colors=ButtonDefaults.buttonColors(containerColor = theme.buttonColor)) {
                Text(text = "Retry")
            }
        }

    }

}