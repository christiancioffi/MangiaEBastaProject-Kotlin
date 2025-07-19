package com.example.mangiabastakotlin.Components

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.mangiabastakotlin.ProfileElementTheme

@Composable
fun ProfileElement(fieldValue: String, label: String, sensible: Boolean, onValueChange: (String)->Unit, maximumLength:Int, placeholder: String){

    var editing by rememberSaveable { mutableStateOf(false) };
    var editedText by rememberSaveable { mutableStateOf(fieldValue) }
    val theme = ProfileElementTheme();

    Log.d("ProfileElement","Executed with fieldValue=$editedText");
    TextField(
        value = editedText,
        onValueChange={newValue ->
            if(newValue.length<=maximumLength){
                editedText=newValue;
                onValueChange(newValue)
            }
        },
        modifier = theme.fieldModifier.then(Modifier.onFocusChanged { focusState: FocusState ->
                editing = focusState.isFocused
            }),
        visualTransformation=if(sensible && !editing) PasswordVisualTransformation() else VisualTransformation.None,
        label = { Text(text=label, style=theme.textLabelStyle) },
        placeholder={
            Text(placeholder)
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = theme.unfocusedContainerColor,
            focusedContainerColor = theme.focusedContainerColor,
            focusedIndicatorColor = theme.focusedIndicatorColor,
            unfocusedIndicatorColor = theme.unfocusedIndicatorColor,
            focusedLabelColor = theme.focusedLabelColor,
            unfocusedLabelColor = theme.unfocusedLabelColor,
            focusedTextColor=theme.focusedTextColor,
            unfocusedTextColor=theme.unfocusedTextColor,
        )
    )
}