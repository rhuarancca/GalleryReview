package com.ebookfrenzy.galleryapp02.ui.maps



import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen(viewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current

    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(multiplePermissionsState.allPermissionsGranted) {
        if (multiplePermissionsState.allPermissionsGranted) {
            viewModel.fetchLocation()
        }
    }

    val location by viewModel.location.collectAsState()

    Scaffold(
        topBar = {
            Spacer(Modifier.size(100.dp))
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text (
                    text = "Galerías disponibles",
                    fontSize = 40.sp
                )
                Text (
                    text = " ",
                    modifier = Modifier.height(30.dp)
                )
                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .height(500.dp)

                ) {
                    if (multiplePermissionsState.allPermissionsGranted) {
                        location?.let { loc ->
                            val cameraPositionState = rememberCameraPositionState()

                            LaunchedEffect(Unit) {
                                val bounds = LatLngBounds.Builder()
                                    .include(viewModel.pointA)
                                    .include(viewModel.pointB)
                                    .include(viewModel.pointC)
                                    .include(LatLng(loc.latitude, loc.longitude))
                                    .build()

                                // Mueve la cámara para mostrar todos los puntos de interés
                                cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 150))
                            }

                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                properties = MapProperties(isMyLocationEnabled = true)
                            ) {
                                Marker(
                                    state = com.google.maps.android.compose.MarkerState(position = viewModel.pointA),
                                    title = "Centro Cultural de la Unsa"
                                )
                                Marker(
                                    state = com.google.maps.android.compose.MarkerState(position = viewModel.pointB),
                                    title = "Centro Cultural Peruano Norteamericano"
                                )
                                Marker(
                                    state = com.google.maps.android.compose.MarkerState(position = viewModel.pointC),
                                    title = "Alianza Francesa de Arequipa"
                                )
                                Marker(
                                    state = com.google.maps.android.compose.MarkerState(position = LatLng(loc.latitude, loc.longitude)),
                                    title = "You are here"
                                )
                            }
                        } ?: run {
                            Text(text = "Getting location...")
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            multiplePermissionsState.launchMultiplePermissionRequest()
                        }
                        Text(text = "Permissions not granted")
                    }
                }

            }
        }
    )
}