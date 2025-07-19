package com.example.mangiabastakotlin.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mangiabastakotlin.NavigationBarTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBar(
    title: String,
    rightButtonText: String?=null,
    leftButtonText: String?=null,
    onClickRightButton: (() ->Unit)? = null,
    onClickLeftButton: (() ->Unit)? = null
){

    val theme=NavigationBarTheme();

    Column(modifier=theme.navigationBarModifier, verticalArrangement = theme.verticalArrangementBar){
        Row(modifier=theme.rowModifier,horizontalArrangement = theme.horizontalArrangementBar){
            Column(modifier=theme.leftButtonContainerModifier.then(Modifier.weight(1f)), verticalArrangement = theme.verticalArrangementContainer, horizontalAlignment = theme.horizontalAlignmentContainer){
                if(leftButtonText!=null && onClickLeftButton!=null){
                    Button(onClick=onClickLeftButton,colors= ButtonDefaults.buttonColors(containerColor = theme.btnColor)){
                        Text(leftButtonText)
                    }
                }
            }
            Column(modifier=theme.titleContainerModifier.then(Modifier.weight(1f)),verticalArrangement = theme.verticalArrangementContainer, horizontalAlignment = theme.horizontalAlignmentContainer){
                Text(title, style=theme.titleStyle)
            }
            Column(modifier=theme.rightButtonContainerModifier.then(Modifier.weight(1f)), verticalArrangement = theme.verticalArrangementContainer, horizontalAlignment = theme.horizontalAlignmentContainer){
                if(rightButtonText!=null && onClickRightButton!=null){
                    Button(onClick=onClickRightButton,colors= ButtonDefaults.buttonColors(containerColor = theme.btnColor)){
                        Text(rightButtonText)
                    }
                }
            }
        }
    }
}