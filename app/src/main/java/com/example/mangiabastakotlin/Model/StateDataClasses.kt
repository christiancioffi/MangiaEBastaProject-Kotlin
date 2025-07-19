package com.example.mangiabastakotlin.Model

import androidx.compose.ui.graphics.ImageBitmap

private val defaultScreen="MenuList";

data class AppState(
    val loadingState: String="Loading",
    val screenStateToBeRestored: ScreenState = ScreenState(),
    val errorMessage: String = ""
)
data class ScreenState(
    val screen: String=defaultScreen,
    val detailedMid: Int = -1,
    val trackedOid:Int=-1,
)

data class ProfileState (
    val loadingState:String="Loading",
    val errorMessage: String ="",
)

data class LastOrderState(
    val loadingState:String="Loading",
    val lastOrder: OrderDetails = OrderDetails(),
    val lastOrderedMenu: MenuLongDetails = MenuLongDetails(),
    val errorMessage: String = "",
)

data class ImageState(
    val loadingState:String="Loading",
    val image: ImageBitmap = ImageBitmap(1,1)
)

data class TrackedOrderState(
    val loadingState:String="Loading",
    val orderDetails: OrderDetails = OrderDetails(),
    val orderedMenu: MenuLongDetails = MenuLongDetails(),
    val errorMessage: String ="",
)

data class MenuListState(
    val loadingState:String="Loading",
    val menuList: List<MenuShortDetails> = listOf<MenuShortDetails>(),
    val errorMessage: String ="",
)

data class DetailedMenuState(
    val loadingState:String="Loading",
    val detailedMenu:MenuLongDetails=MenuLongDetails(),
    val errorMessage: String ="",
)
