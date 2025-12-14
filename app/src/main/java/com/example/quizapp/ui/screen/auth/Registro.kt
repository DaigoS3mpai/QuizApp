package com.example.quizapp.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.R
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    val state by viewModel.register.collectAsState()

    var buttonsVisible by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) { buttonsVisible = true }

    LaunchedEffect(state.success, state.createdUserId) {
        val userId = state.createdUserId
        if (state.success && userId != null) {
            viewModel.clearRegisterResult()
            navController.navigate(Route.SetupSecurityQuestions.createRoute(userId)) {
                popUpTo(Route.Registro.path) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = { AppTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Aplicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )

            // Campo Usuario
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.nameError != null,
                supportingText = {
                    state.nameError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            )

            // Campo Correo
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onRegisterEmailChange,
                label = { Text("Correo Electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.emailError != null,
                supportingText = {
                    state.emailError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            )

            // Campo Contraseña con botón Mostrar/Ocultar
            OutlinedTextField(
                value = state.pass,
                onValueChange = viewModel::onRegisterPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(
                        onClick = { passwordVisible = !passwordVisible },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(if (passwordVisible) "Ocultar" else "Mostrar", fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = state.passError != null,
                supportingText = {
                    state.passError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            )

            // Campo Confirmar Contraseña con botón Mostrar/Ocultar
            OutlinedTextField(
                value = state.confirm,
                onValueChange = viewModel::onConfirmChange,
                label = { Text("Confirmar Contraseña") },
                singleLine = true,
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(
                        onClick = { confirmVisible = !confirmVisible },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(if (confirmVisible) "Ocultar" else "Mostrar", fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = state.confirmError != null,
                supportingText = {
                    state.confirmError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            )

            // Botones
            AnimatedVisibility(
                visible = buttonsVisible,
                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Botón Registrar
                    Button(
                        onClick = { viewModel.submitRegister() },
                        enabled = state.canSubmit && !state.isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF58B956),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = if (state.isSubmitting) "Creando..." else "Registrarse",
                            fontSize = 20.sp
                        )
                    }

                    // Mensaje de error
                    state.errorMsg?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Botón Volver
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF58B956),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Volver", fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()
    Registro(navController)
}
