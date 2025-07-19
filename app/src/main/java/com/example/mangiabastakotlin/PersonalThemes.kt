package com.example.mangiabastakotlin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val backgroundColor=Color(android.graphics.Color.parseColor("#A89F91"));
private val initialBackgroundColor=Color(android.graphics.Color.parseColor("#FF7F00"));
private val menuBckgColor=Color(android.graphics.Color.parseColor("#D6C6A1"));
private val borderColor=Color(android.graphics.Color.parseColor("#263238"));
private val navigationBarColor=Color(android.graphics.Color.parseColor("#263238"));
private val buttonColor=Color(android.graphics.Color.parseColor("#FF8C00"));
private val retryButtonColor=Color(android.graphics.Color.parseColor("#7F7A65"));
private val textInputTextColor=Color(android.graphics.Color.parseColor("#555555"));
private val completedTextColor=Color(android.graphics.Color.parseColor("#43aa8b"));
private val onDeliveryTextColor=Color(android.graphics.Color.parseColor("#dc2f02"));
private val border: RoundedCornerShape=RoundedCornerShape(8.dp);


private data class TitleTheme(
    val titleModifier: Modifier= Modifier
        .drawBehind {
            val underlineY = size.height + 2.dp.toPx()
            drawLine(
                start = Offset(0f, underlineY),
                end = Offset(size.width, underlineY),
                color = borderColor,
                strokeWidth = 8f
            )
        },
    val titleStyle: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize=25.sp),
    val separatorModifier: Modifier = Modifier.height(15.dp),
)

data class InitialLoadingScreenTheme(
    val columnModifier : Modifier = Modifier
        .background(initialBackgroundColor)
        .fillMaxWidth()
        .fillMaxHeight(),
    val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val verticalArrangement: Arrangement.HorizontalOrVertical =Arrangement.Center,
    val innerVerticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    val logoTextStyle: TextStyle = TextStyle(color=Color.Black,fontSize=30.sp,fontWeight= FontWeight.Bold)
)

data class LoadingScreenTheme(
    val columnModifier : Modifier = Modifier
        .background(backgroundColor)
        .fillMaxWidth()
        .fillMaxHeight(),
    val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val verticalArrangement: Arrangement.HorizontalOrVertical =Arrangement.Center,
    val innerVerticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
)

data class ErrorScreenTheme(
    val columnModifier : Modifier = Modifier
        .background(backgroundColor)
        .fillMaxWidth()
        .fillMaxHeight(),
    val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center,
    val innerVerticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    val buttonColor: Color=retryButtonColor,
)

data class MenuListTheme(
    val lazyColumnModifier : Modifier = Modifier
        .background(backgroundColor)
        .fillMaxWidth()
        .fillMaxHeight(),
    val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val separatorModifier: Modifier=Modifier.height(15.dp),
    val titleModifier: Modifier= TitleTheme().titleModifier,
    val titleStyle: TextStyle = TitleTheme().titleStyle,
)

data class MenuElementTheme(
    val columnModifier : Modifier = Modifier
        .background(menuBckgColor, border)
        .fillMaxWidth(fraction=0.9f)
        .border(width=3.dp, borderColor, border)
        .clip(border),
    val imageModifierContainer: Modifier=Modifier
        .fillMaxWidth()
        .height(150.dp),
    val menuInfoColumnModifier:Modifier=Modifier
        .fillMaxWidth()
        .padding(10.dp),
    val verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(5.dp),
    val textLabelStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
)


data class MenuDetailsTheme(
    val columnModifier : Modifier = Modifier
        .background(menuBckgColor)
        .fillMaxWidth()
        .fillMaxHeight(),
    val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val menuInfoColumnModifier:Modifier=Modifier
        .fillMaxWidth()
        .padding(10.dp),
    val verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    val imageModifierContainer: Modifier=Modifier
        .fillMaxWidth(fraction=0.9f)
        .height(300.dp)
        .border(width=3.dp, borderColor, border)
        .clip(border),
    val titleModifier: Modifier= TitleTheme().titleModifier,
    val titleStyle: TextStyle = TitleTheme().titleStyle,
    val separatorModifier: Modifier=TitleTheme().separatorModifier,
    val textLabelStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
)

data class ButtonWithLoadingIndicatorTheme(
    val rowAlignment: Alignment.Vertical =Alignment.CenterVertically,
    val horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(8.dp),
    val color: Color = buttonColor,
    val textStyle: TextStyle=TextStyle(fontSize=15.sp),
    val indicatorModifier: Modifier=Modifier
        .width(15.dp)
        .height(15.dp)
)

data class OrderTrackingTheme(
    val columnModifier: Modifier=Modifier
        .background(backgroundColor)
        .fillMaxWidth()
        .fillMaxHeight(),
    val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val titleModifier: Modifier= TitleTheme().titleModifier,
    val titleStyle: TextStyle = TitleTheme().titleStyle,
    val separatorModifier: Modifier=TitleTheme().separatorModifier,
    val boxModifier: Modifier=Modifier
        .height(450.dp)
        .fillMaxWidth(fraction=0.99f)
        .border(width=3.dp, borderColor, border)
        .clip(border),
    val orderInfoColumnModifier:Modifier=Modifier
        .background(menuBckgColor, border)
        .border(width=3.dp, borderColor, border)
        .fillMaxWidth(fraction = 0.75f)
        .padding(10.dp),
    val verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    val textLabelStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
    val completedTextStyle: SpanStyle=SpanStyle(color=completedTextColor),
    val onDeliveryTextStyle: SpanStyle=SpanStyle(color=onDeliveryTextColor),
    val timerTheme: TimerTheme=TimerTheme(),
)

data class OrderTrackingMapTheme(
    val boxModifier: Modifier=Modifier
        .height(450.dp)
        .fillMaxWidth(fraction=0.99f)
        .border(width=3.dp, borderColor, border)
        .clip(border),
)

data class TimerTheme(
    val textLabelStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
)

data class NavigationBarTheme(
    val btnColor:Color = buttonColor,
    val navigationBarModifier:Modifier=Modifier
        .fillMaxWidth()
        .fillMaxHeight(fraction = 0.12f)
        .background(navigationBarColor),
    val verticalArrangementBar: Arrangement.Vertical = Arrangement.Bottom,
    val horizontalArrangementBar: Arrangement.Horizontal = Arrangement.Center,
    val rowModifier: Modifier = Modifier
        .fillMaxHeight(fraction=0.6f)
        .fillMaxWidth(),
    val leftButtonContainerModifier: Modifier = Modifier
        .fillMaxHeight(),
    val titleContainerModifier:Modifier = Modifier
        .fillMaxHeight(),
    val rightButtonContainerModifier: Modifier = Modifier
        .fillMaxHeight(),
    val verticalArrangementContainer: Arrangement.Vertical = Arrangement.Center,
    val horizontalAlignmentContainer: Alignment.Horizontal = Alignment.CenterHorizontally,
    val leftButtonModifier: Modifier = Modifier
        .fillMaxWidth(),
    val titleStyle: TextStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color=Color.White),
    val rightButtonModifier: Modifier = Modifier
        .fillMaxWidth(),
)

data class ProfileTheme(
    val columnModifier : Modifier = Modifier
        .background(backgroundColor)
        .fillMaxWidth()
        .fillMaxHeight(),
    val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val titleModifier: Modifier= TitleTheme().titleModifier,
    val titleStyle: TextStyle = TitleTheme().titleStyle,
    val separatorModifier: Modifier=TitleTheme().separatorModifier,
)

data class ProfileElementTheme(
    val fieldModifier : Modifier = Modifier
        .background(Color.Transparent)
        .fillMaxWidth(fraction=0.9f)
        .drawBehind {
            val underlineY = size.height + 2.dp.toPx()
            drawLine(
                start = Offset(0f, underlineY),
                end = Offset(size.width, underlineY),
                color = borderColor,
                strokeWidth = 8f
            )
        },
    val textLabelStyle: TextStyle = TextStyle(fontWeight = FontWeight.Bold),
    val unfocusedContainerColor : Color = Color.Transparent, // Impedisce il colore di sfondo blu predefinito
    val focusedContainerColor : Color = Color.Transparent, // Impedisce il colore di sfondo blu predefinito
    val focusedIndicatorColor : Color = Color.Transparent, // Indicato di focus trasparente
    val unfocusedIndicatorColor : Color = Color.Transparent, // Indicato non focalizzato trasparente,
    val focusedLabelColor : Color = Color.Black, // Colore del label quando il TextField è focalizzato (opzionale)
    val unfocusedLabelColor : Color = Color.Black, // Colore del label quando il TextField non è focalizzato (opzionale)
    val focusedTextColor : Color =Color.Black,
    val unfocusedTextColor : Color = textInputTextColor,
)

data class LastOrderElementTheme(
    val columnModifier : Modifier = Modifier
        .background(menuBckgColor, border)
        .fillMaxWidth(fraction=0.8f)
        .border(width=3.dp, borderColor, border)
        .clip(border),
    val horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val menuInfoColumnModifier:Modifier=Modifier
        .fillMaxWidth()
        .padding(10.dp),
    val verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(5.dp),
    val imageModifierContainer: Modifier=Modifier
        .fillMaxWidth()
        .height(150.dp),
    val textLabelStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
    val textErrorStyle: TextStyle = TextStyle(fontWeight = FontWeight.Bold),
    val completedTextStyle: SpanStyle=SpanStyle(color=completedTextColor),
    val onDeliveryTextStyle: SpanStyle=SpanStyle(color=onDeliveryTextColor),
    val titleModifier: Modifier= TitleTheme().titleModifier,
    val titleStyle: TextStyle = TitleTheme().titleStyle,
    val separatorModifier: Modifier=TitleTheme().separatorModifier,
)






