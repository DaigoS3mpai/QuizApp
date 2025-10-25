package com.example.quizapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.R
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
// Observamos el estado del registro desde el ViewModel
    val state by viewModel.register.collectAsState()
    var buttonsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        buttonsVisible = true
    }
    Scaffold(
        topBar = {
            AppTopBar()
        }
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
            Image(
                painterResource(R.drawable.logo),
                contentDescription = "Logo Aplicacion",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Fit
            )

            // Campo de usuario (nombre)
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

            // Campo de contraseña
            OutlinedTextField(
                value = state.pass,
                onValueChange = viewModel::onRegisterPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.passError != null,
                supportingText = {
                    state.passError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            )

            // Campo de confirmación
            OutlinedTextField(
                value = state.confirm,
                onValueChange = viewModel::onConfirmChange,
                label = { Text("Confirmar Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.confirmError != null,
                supportingText = {
                    state.confirmError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            )

            // Campo de correo electrónico
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
            AnimatedVisibility(
                visible = buttonsVisible,

                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
            // Botón "Registrarse"
            Button(
                onClick = {
                    viewModel.submitRegister()
                    if (state.success) {
                        viewModel.clearRegisterResult()
                        navController.navigate(Route.Perfil.path)
                    }
                },
                enabled = state.canSubmit && !state.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (state.isSubmitting) "Creando..." else "Registrarse",
                    fontSize = 22.sp
                )
            }

            // Mensaje de error general
            state.errorMsg?.let {
                Text(text = it, color = Color.Red, fontSize = 14.sp)
            }

            }
            AnimatedVisibility(
                visible = buttonsVisible,

                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
            // Botón volver
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                )
            ) {
                Text("Volver", fontSize = 22.sp)
            }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun registerScreenPreview() {
    val navController = rememberNavController()
    Registro(navController)
}
