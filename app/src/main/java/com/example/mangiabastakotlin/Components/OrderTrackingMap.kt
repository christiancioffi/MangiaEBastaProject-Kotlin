package com.example.mangiabastakotlin.Components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mangiabastakotlin.OrderTrackingMapTheme
import com.example.mangiabastakotlin.R
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlin.reflect.KSuspendFunction4

@Composable
fun OrderTrackingMap(setAnnotationLocations: KSuspendFunction4<PointAnnotationManager, Bitmap, Bitmap, Bitmap, Unit>){

    val context = LocalContext.current;
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
        inSampleSize = 3
    }

    val restaurantIcon = BitmapFactory.decodeResource(context.resources, R.drawable.restaurant_position_marker, options)
    val droneIcon = BitmapFactory.decodeResource(context.resources, R.drawable.drone_position_marker, options)
    val deliveryIcon = BitmapFactory.decodeResource(context.resources, R.drawable.delivery_position_marker, options)


    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(10.0)
        }
    };

    val theme= OrderTrackingMapTheme();

    Box(
        modifier = theme.boxModifier
    ) {
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState
        ) {
            MapEffect(Unit) { mapView ->
                mapView.location.updateSettings {
                    locationPuck = createDefault2DPuck(withBearing = true)
                    puckBearingEnabled = true
                    puckBearing = PuckBearing.HEADING
                    enabled = true
                }
                mapViewportState.transitionToFollowPuckState();
                val pointAnnotationManager: PointAnnotationManager = mapView.annotations.createPointAnnotationManager();
                setAnnotationLocations(pointAnnotationManager, restaurantIcon, droneIcon, deliveryIcon);
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)  // Allinea la colonna in alto a destra
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    mapViewportState.easeTo(
                        cameraOptions {
                            zoom((mapViewportState.cameraState?.zoom ?: 0.0) + 1.0)
                        }
                    )
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }

            Spacer(modifier = Modifier.height(8.dp))

            FloatingActionButton(
                onClick = {
                    mapViewportState.easeTo(
                        cameraOptions {
                            zoom((mapViewportState.cameraState?.zoom ?: 0.0) - 1.0)
                        }
                    )
                }
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }
        }
    }

}