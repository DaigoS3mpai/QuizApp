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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.quizapp.R
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Password(
    navController: NavHostController,
    email: String,
    token: String
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val state by viewModel.password.collectAsState()

    var buttonsVisible by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        buttonsVisible = true
        viewModel.onPasswordEmailChange(email)
        viewModel.onPasswordTokenChange(token)
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            viewModel.clearPasswordResult()
            navController.navigate(Route.Login.path) {
                popUpTo(Route.Login.path) { inclusive = true }
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Aplicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )

            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            OutlinedTextField(
                value = state.newPass,
                onValueChange = viewModel::onPasswordNewPassChange,
                label = { Text("Nueva contraseña") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(
                        onClick = { passwordVisible = !passwordVisible },
                        contentPadding = PaddingValues(0.dp)
                    ) { Text(if (passwordVisible) "Ocultar" else "Mostrar", fontSize = 12.sp) }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = state.newPassError != null,
                supportingText = { state.newPassError?.let { Text(it, color = Color.Red, fontSize = 12.sp) } }
            )

            OutlinedTextField(
                value = state.confirmPass,
                onValueChange = viewModel::onPasswordConfirmChange,
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(
                        onClick = { confirmVisible = !confirmVisible },
                        contentPadding = PaddingValues(0.dp)
                    ) { Text(if (confirmVisible) "Ocultar" else "Mostrar", fontSize = 12.sp) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                isError = state.confirmPassError != null,
                supportingText = { state.confirmPassError?.let { Text(it, color = Color.Red, fontSize = 12.sp) } }
            )

            AnimatedVisibility(
                visible = buttonsVisible,
                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
                Column {
                    Button(
                        onClick = { viewModel.submitPasswordRecoveryReset() },
                        enabled = !state.isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF58B956),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (state.isSubmitting) "Actualizando..." else "Actualizar contraseña",
                            fontSize = 18.sp
                        )
                    }

                    state.errorMsg?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

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
                        Text("Volver", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
