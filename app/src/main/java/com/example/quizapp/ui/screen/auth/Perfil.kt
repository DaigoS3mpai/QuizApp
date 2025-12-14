package com.example.quizapp.ui.screen.auth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.quizapp.R
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.component.CircularImageButton
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Crea un archivo temporal en cache para la cámara
private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply { if (!exists()) mkdirs() }
    return File(storageDir, "IMG_$timeStamp.jpg")
}

// Obtiene el Uri usando el FileProvider
private fun getTempImageUri(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Perfil(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    // Cargar sesión (incluye la foto desde StorageHelper)
    LaunchedEffect(Unit) { viewModel.loadSession() }

    val currentUser by viewModel.currentUser.collectAsState()

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador para galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            viewModel.onProfileImageSelected(it)   // guarda + actualiza currentUser.photo
            Toast.makeText(context, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador para cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCaptureUri != null) {
            profileImageUri = pendingCaptureUri
            viewModel.onProfileImageSelected(pendingCaptureUri) // guarda + actualiza currentUser.photo
            Toast.makeText(context, "Foto capturada correctamente", Toast.LENGTH_SHORT).show()
        } else {
            pendingCaptureUri = null
            Toast.makeText(context, "Error al capturar foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val photoFile = createTempImageFile(context)
            val photoUri = getTempImageUri(context, photoFile)
            pendingCaptureUri = photoUri
            cameraLauncher.launch(photoUri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Permiso de almacenamiento (para Android < 13)
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
        }
    }

    var buttonsVisible by remember { mutableStateOf(false) }

    // Mostrar botones con animación al entrar a la pantalla
    LaunchedEffect(Unit) {
        buttonsVisible = true
    }

    Scaffold(topBar = { AppTopBar(showBackButton = true,
        onBackClick = {
            navController.navigate(Route.MenuOpciones.path) {
                // Opcional: limpiar el backstack para que no vuelva a Perfil/Login
                popUpTo(Route.MenuOpciones.path) { inclusive = false }
                launchSingleTop = true
            }
        }
    )
    }) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
        ) {

            // Botón para Editar perfil (nombre/correo/contraseña)
            CircularImageButton(
                imageRes = R.drawable.lapiz,
                onClick = { navController.navigate(Route.EditProfile.path) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Imagen de perfil
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    val painter = when {
                        // 1) Si recién seleccionaste una imagen (galería/cámara)
                        profileImageUri != null -> rememberAsyncImagePainter(profileImageUri)
                        // 2) Si la sesión tiene foto cargada desde StorageHelper
                        currentUser.photo != null -> rememberAsyncImagePainter(currentUser.photo)
                        // 3) Si no hay foto, usamos la imagen por defecto
                        else -> painterResource(R.drawable.perfil)
                    }

                    Image(
                        painter = painter,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Botón: Seleccionar desde galería
                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                    exit = scaleOut(animationSpec = tween(500))
                ) {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                    galleryLauncher.launch("image/*")
                                } else {
                                    storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                            } else {
                                // Android 13+ no requiere permiso para leer imágenes locales
                                galleryLauncher.launch("image/*")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF58B956),
                            contentColor = Color.Black
                        )
                    ) { Text("Seleccionar desde galería", fontSize = 18.sp) }
                }

                // Botón: Tomar foto con cámara
                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = scaleIn(animationSpec = tween(500, delayMillis = 200)),
                    exit = scaleOut(animationSpec = tween(500))
                ) {
                    Button(
                        onClick = {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            )
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                val photoFile = createTempImageFile(context)
                                val photoUri = getTempImageUri(context, photoFile)
                                pendingCaptureUri = photoUri
                                cameraLauncher.launch(photoUri)
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF58B956),
                            contentColor = Color.Black
                        )
                    ) { Text("Tomar Foto con Cámara", fontSize = 18.sp) }
                }

                // Información del usuario
                Text("Usuario: ${currentUser.name}", fontSize = 25.sp)
                Text("Correo: ${currentUser.email}", fontSize = 20.sp)
                Text(
                    text = if (currentUser.loggedIn) "Sesión activa" else "Sin sesión",
                    fontSize = 18.sp,
                    color = if (currentUser.loggedIn) Color.Green else Color.Red
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.Black, thickness = 1.dp)
                Text("🏅 Puntaje actual: ${currentUser.puntaje}", fontSize = 22.sp)
                Text("🌍 Puntaje global: ${currentUser.puntajeGlobal}", fontSize = 22.sp)
                Divider(color = Color.Black, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = scaleIn(animationSpec = tween(500, delayMillis = 250)),
                    exit = scaleOut(animationSpec = tween(500))
                ) {
                    Button(
                        onClick = { navController.navigate(Route.MisPartidas.path) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF58B956),
                            contentColor = Color.Black
                        )
                    ) { Text("Ver mis partidas", fontSize = 20.sp) }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.Black, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Volver al menú
                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = scaleIn(animationSpec = tween(500, delayMillis = 300)),
                    exit = scaleOut(animationSpec = tween(500))
                ) {
                    Button(
                        onClick = { navController.navigate(Route.MenuOpciones.path) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF58B956),
                            contentColor = Color.Black
                        )
                    ) { Text("Inicio", fontSize = 25.sp) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    val navController = rememberNavController()
    Perfil(navController)
}
