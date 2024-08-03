package com.ebookfrenzy.galleryapp02.ui.qr


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScanQrScreen(navController: NavController) {
    val context = LocalContext.current
    var scanResult by remember { mutableStateOf("") }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(scanResult) {
        if (scanResult.isNotEmpty()) {
            val paintingId = scanResult.trim()
            navController.navigate("qrResult/$paintingId")
        }
    }

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
                Text(
                    text = "Escanera el código",
                    fontSize = 40.sp
                )
                Text(
                    text = " ",
                    modifier = Modifier.height(30.dp)
                )

                if (hasCameraPermission) {
                    Box(
                        modifier = Modifier
                            .width(350.dp)
                            .height(500.dp)
                    ) {
                        val barcodeView = remember { CompoundBarcodeView(context) }
                        AndroidView({ barcodeView }) { view ->
                            view.decodeContinuous(object : BarcodeCallback {
                                override fun barcodeResult(result: BarcodeResult) {
                                    if (result.text != scanResult) {
                                        scanResult = result.text
                                    }
                                }

                                override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {}
                            })
                            view.resume() // Iniciar el escaneo
                        }

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (scanResult.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            Spacer(modifier = Modifier.weight(3f))
      /*                      Button(onClick = { /* Alguna acción adicional si es necesario */ }) {
                                Text(text = "Otra Acción")
                            }*/
                        }
                    }
                } else {
                    Text(text = "Permissions not granted")
                }
            }
        }
    )
}
