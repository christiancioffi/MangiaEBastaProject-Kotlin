package com.example.mangiabastakotlin.Components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.mangiabastakotlin.TimerTheme
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
fun Timer(timestamp:Long){

    var timer by rememberSaveable{ mutableStateOf("...") }

    LaunchedEffect(Unit){
        var counter:Long=(timestamp-Instant.now().toEpochMilli())/1000;

        while(counter>0L){
            val days = counter / (60*60*24)
            val hours = (counter % (60*60*24)) / (60*60)
            val minutes = (counter % (60*60)) / 60
            val seconds = counter % 60

            val countingDays=when(days){
                0L -> ""
                1L -> "$days day "
                else -> "$days days "
            }

            val countingHours=when(hours){
                0L -> ""
                1L -> "$hours hour "
                else -> "$hours hours "
            }

            val countingMinutes=when(minutes){
                0L -> ""
                1L -> "$minutes minute "
                else -> "$minutes minutes "
            }

            val countingSeconds=when(seconds){
                0L -> ""
                1L -> "$seconds second "
                else -> "$seconds seconds "
            }

            timer="$countingDays$countingHours$countingMinutes$countingSeconds";
            delay(1000);
            counter-=1;
        }
        timer="a few seconds...";
    }
    val theme= TimerTheme();
    val timerText= buildAnnotatedString {
        withStyle(style=theme.textLabelStyle){
            append("Order coming in ")
        }
        append(timer)
    }
    Text(text=timerText, softWrap=true)
}