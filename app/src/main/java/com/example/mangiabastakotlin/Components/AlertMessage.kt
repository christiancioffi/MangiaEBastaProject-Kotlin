package com.example.mangiabastakotlin.Components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AlertMessage(title:String, message: String, onConfirmation: ()->Unit) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation();
                }
            ) {
                Text("Ok")
            }
        },
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = false, // Disabilita la chiusura tramite il tasto "Indietro"
            dismissOnClickOutside = false // Disabilita la chiusura toccando fuori
        )
    )
}