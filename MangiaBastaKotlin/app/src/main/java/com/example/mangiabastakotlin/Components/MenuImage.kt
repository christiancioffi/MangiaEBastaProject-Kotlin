package com.example.mangiabastakotlin.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.mangiabastakotlin.Model.ImageState
import com.example.mangiabastakotlin.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MenuImage(modifier: Modifier = Modifier, state: StateFlow<ImageState> = MutableStateFlow<ImageState>(ImageState(loadingState = "Error"))){
    val imageState=state.collectAsState();

    Column(modifier=modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        when(imageState.value.loadingState){
            "Loading" -> CircularProgressIndicator(color= Color.White)
            "Error" -> {
                Image(
                    painter = painterResource(id = R.drawable.default_food),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )
            }
            "Loaded" -> {
                Image(
                    bitmap = imageState.value.image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}