package com.example.quizapp.ui.screen

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun Login(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
// Observamos el estado del login desde el ViewModel
    val state by viewModel.login.collectAsState()
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
            // Logo de la aplicación
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Aplicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            // Campo usuario o email
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onLoginEmailChange, // ya usa validateLoginUserOrEmail
                label = { Text("Usuario o correo") },        // solo cambiamos la etiqueta
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.emailError != null,
                supportingText = {
                    state.emailError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            )

            // Campo contraseña
            OutlinedTextField(
                value = state.pass,
                onValueChange = viewModel::onLoginPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                isError = state.passError != null,
                supportingText = {
                    state.passError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            )

            // Botón iniciar sesión
            Button(
                onClick = {
                    viewModel.submitLogin()
                    if (state.success) {
                        viewModel.clearLoginResult()
                        navController.navigate(Route.MenuOpciones.path)
                    }
                },
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
                    text = if (state.isSubmitting) "Iniciando..." else "Iniciar Sesión",
                    fontSize = 18.sp
                )
            }

            // Mensaje de error general
            state.errorMsg?.let {
                Text(text = it, color = Color.Red, fontSize = 14.sp)
            }

            // Botón volver
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver", fontSize = 18.sp)
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    Login(navController)
}
