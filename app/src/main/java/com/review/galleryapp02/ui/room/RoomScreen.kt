package com.ebookfrenzy.galleryapp02.ui.room

import androidx.compose.ui.Alignment

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ebookfrenzy.galleryapp02.data.model.LocationModel
import com.ebookfrenzy.galleryapp02.data.model.Painting
import com.ebookfrenzy.galleryapp02.ui.painting.PaintingViewModel
import com.google.maps.android.compose.Circle
import com.review.galleryapp02.data.model.Position
import com.review.galleryapp02.ui.nav.BottomNavItem

@Composable
fun RoomScreen(
    navController: NavController,
    roomId: String,
    roomHeightMeters: Float = 5.5f, // Altura de la habitación en metros
    roomWidthMeters: Float = 5f, // Ancho de la habitación en metros
    viewModel: RoomViewModel = hiltViewModel(),
    viewModel2: PaintingViewModel = hiltViewModel(),
    paintingOffsets: List<Pair<Float, Float>> = listOf(
        -0.5f to 2f, // Imagen 1 (izquierda supp)
        -0.5f to 6f, // Imagen 2 (izquierda inf)
        2.2f to 9f, // Imagen 3 (medio inf)
        4.5f to 3f, // Imagen 4 (derecha)
    )
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.entries.all { it.value }
        if (allPermissionsGranted) {
            Log.d("RoomScreen", "All permissions granted. Starting beacon scanning...")
            viewModel.startScanning()
        } else {
            Log.d("RoomScreen", "Permissions not granted.")
            // Manejar el caso donde los permisos no fueron otorgados
        }
    }

    LaunchedEffect(roomId) {
        viewModel.getRoomById("galleryId1", roomId)
        //requestPermissionsAndStartScanning(activity, requestPermissionLauncher, viewModel)
    }

    val room by viewModel.room.collectAsState()
    val userPosition by viewModel.userPosition.collectAsState(initial = null)
    val userPositionV2 = Position(2.0f, 1.0f)

    room?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                Button(
                    onClick = {
                        navController.popBackStack()
                    }, shape = CutCornerShape(6.dp), modifier = Modifier
                        .size(width = 70.dp, height = 50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Image(
                        painter = painterResource(id = BottomNavItem.returnButton.icon),
                        contentDescription = null,
                    )

                }
                Spacer(modifier = Modifier.width(45.dp))
                Text(
                    text = it.name,
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.width(45.dp))
                Text(
                    text = "  ",
                    fontSize = 30.sp
                )
            }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((roomHeightMeters * 100).dp) // Ajustar altura basada en metros
                        .padding(16.dp)
                ) {
                    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

                    Canvas(modifier = Modifier
                        .matchParentSize()
                        .onGloballyPositioned { coordinates ->
                            canvasSize = coordinates.size
                        }) {
                        // Dibujar el rectángulo de la habitación con esquinas redondeadas
                        drawRoundRect(
                            color = Color.Black,
                            size = canvasSize.toSize(),
                            style = Stroke(width = 8f) // Ajustar el grosor de la línea
                        )
                    }

                    // Dibujar las imágenes dentro del rectángulo
                    val circleRadius = 30f
                    val circleDiameter = circleRadius * 2

                    val paintingState = viewModel2.paintings.collectAsState()
                    val excludedIds = listOf("paintingId1", "paintingId2", "paintingId3", "paintingId4")

                    val paintings = paintingState.value
                    paintings.data?.let { paintingList ->
                        val filteredPaintings = paintingList.filterNot { it.id in excludedIds }

                        paintingOffsets.forEachIndexed { index, (offsetX, offsetY) ->
                            if (index < it.imageUrls.size) {
                                val imageUrl = it.imageUrls[index]
                                ImageWithOffset(
                                    painting = filteredPaintings[index],
                                    position = Offset(
                                        offsetX * 100,
                                        offsetY * 100
                                    ), // Convertir metros a unidades canvas
                                    size = circleDiameter.dp,
                                    title = it.name,
                                    navController = navController// Pasar el nombre de la pintura como título
                                )
                            }
                        }

                        // Dibujar la posición del usuario
                        userPosition?.let { position ->
                            Canvas(modifier = Modifier.matchParentSize()) {
                                drawCircle(
                                    color = Color.Red,
                                    radius = circleRadius,
                                    center = Offset(
                                        userPositionV2.Xposition * canvasSize.width / roomWidthMeters,
                                        userPositionV2.Yposition * canvasSize.height / roomHeightMeters
                                    )
                                )
                            }
                        }
                    }
                    /*Spacer(modifier = Modifier.height(16.dp))
                Text(text = it.description, modifier = Modifier.align(Alignment.CenterHorizontally))
    */
                }
            } ?: run {
                // Text(text = "Room not found", modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
            }
        }
    }
    @Composable
    fun ImageWithOffset(
        painting: Painting,
        position: Offset,
        size: Dp,
        title: String,
        navController: NavController
    )
    {
        Column(
            modifier = Modifier
                .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
                .size(size)
                .clickable {
                    navController.navigate("paintingDetail/${painting.id}")
                    /*
                navController.currentBackStackEntry?.savedStateHandle?.set("imageUrl", imageUrl)
                navController.navigate("roomPaintingDetail")*/
                }
        ) {
            AsyncImage(
                model = painting.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(
                        RoundedCornerShape(
                            topEnd = 30.dp,
                            topStart = 30.dp,
                            bottomEnd = 30.dp,
                            bottomStart = 30.dp
                        )
                    )
                    .border(3.dp, color = Color.Black)
            )
            Text(
                text = title, modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }

    fun requestPermissionsAndStartScanning(
        activity: Activity?,
        requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
        viewModel: RoomViewModel
    ) {
        val permissions = listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(activity!!, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            Log.d("RoomScreen", "Requesting permissions: $permissionsToRequest")
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            Log.d("RoomScreen", "Permissions already granted. Starting beacon scanning...")
            viewModel.startScanning()
        }
}