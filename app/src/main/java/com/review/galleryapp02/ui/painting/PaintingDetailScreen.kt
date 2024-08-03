package com.ebookfrenzy.galleryapp02.ui.painting

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.ebookfrenzy.galleryapp02.R
import com.ebookfrenzy.galleryapp02.utils.Resource
import com.review.galleryapp02.ui.nav.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PaintingDetailScreen(
    navController: NavHostController,
    paintingId: String,
    viewModel: PaintingViewModel = hiltViewModel()) {

    val paintingState = viewModel.paintings.collectAsState()
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    Scaffold(
        topBar = {
                Spacer(Modifier.size(100.dp))
            },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (val paintings = paintingState.value) {
                        is Resource.Loading -> CircularProgressIndicator()
                        is Resource.Success -> {
                            paintings.data?.firstOrNull { it.id == paintingId }
                                ?.let { painting ->
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row {
                                            Button(
                                                onClick = {
                                                    navController.popBackStack()
                                                }, shape = CutCornerShape(6.dp)
                                                , modifier = Modifier
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
                                            Spacer(modifier = Modifier.width(15.dp))

                                            Column () {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = painting.artist,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Text(
                                                    text = painting.title,
                                                    style = MaterialTheme.typography.titleLarge
                                                )
                                            }

                                        }
                                        Spacer(modifier = Modifier.height(30.dp))
                                        Image(
                                            painter = rememberAsyncImagePainter(painting.imageUrl),
                                            contentDescription = painting.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(400.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Agrega aquí más detalles de la pintura si es necesario
                                        Text(
                                            text = painting.description,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                if (isPlaying) {
                                                    mediaPlayer?.stop()
                                                    mediaPlayer?.reset()
                                                    mediaPlayer?.release()
                                                    mediaPlayer = null
                                                    isPlaying = false
                                                } else {
                                                    mediaPlayer = MediaPlayer().apply {
                                                        setDataSource(painting.audioUrl)
                                                        prepare()
                                                        start()
                                                        setOnCompletionListener {
                                                            isPlaying = false
                                                        }
                                                    }
                                                    isPlaying = true
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Black,
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(if (isPlaying) "Detener Audio" else "Iniciar Audio")
                                        }
                                    }
                                } ?: run {
                                Text(text = "Painting not found")
                            }
                        }

                        is Resource.Error -> {
                            Text(text = paintings.message ?: "An error occurred")
                        }

                        else -> {}
                    }
                }
            }
        }
    )
}
