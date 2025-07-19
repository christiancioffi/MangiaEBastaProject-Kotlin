package com.example.mangiabastakotlin.Components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mangiabastakotlin.Model.ScreenState


@Composable
fun Screen(navController: NavHostController, firstScreen: ScreenState){

    NavHost(
        navController = navController,
        startDestination = firstScreen.screen,
    ) {
        composable("MenuList") {
            MenuList(navController);
        }
        composable("MenuDetails/{mid}") { backStackEntry ->
            val mid:Int = (backStackEntry.arguments?.getString("mid") ?: "").toIntOrNull() ?: firstScreen.detailedMid
            MenuDetails(navController,mid);
        }
        composable("OrderTracking/{oid}") {backStackEntry ->
            val oid:Int = (backStackEntry.arguments?.getString("oid") ?: "").toIntOrNull() ?: firstScreen.trackedOid;
            OrderTracking(navController, oid);
        }
        composable("Profile") {
            Profile(navController)
        }
    }



}


