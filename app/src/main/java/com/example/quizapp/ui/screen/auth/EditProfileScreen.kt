package com.example.quizapp.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModelFactory

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(LocalContext.current)
    )
) {
    val state by authViewModel.editProfile.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Cargar sesión
    LaunchedEffect(Unit) {
        authViewModel.loadSession()
    }

    // Inicializar formulario cuando haya usuario logueado
    LaunchedEffect(currentUser.loggedIn) {
        if (currentUser.loggedIn) {
            authViewModel.initEditProfile()
        }
    }

    Scaffold(
        topBar = { AppTopBar() }
    ) { padding ->

        // Si no hay usuario logueado mostramos un mensaje simple
        if (!currentUser.loggedIn) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFF87CEEB)),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay usuario autenticado.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // NOMBRE
            OutlinedTextField(
                value = state.name,
                onValueChange = authViewModel::onEditNameChange,
                label = { Text("Nombre") },
                isError = state.nameError != null,
                supportingText = {
                    state.nameError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // CORREO
            OutlinedTextField(
                value = state.email,
                onValueChange = authViewModel::onEditEmailChange,
                label = { Text("Correo") },
                isError = state.emailError != null,
                supportingText = {
                    state.emailError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // CONTRASEÑA ACTUAL
            OutlinedTextField(
                value = state.currentPass,
                onValueChange = authViewModel::onEditCurrentPassChange,
                label = { Text("Contraseña actual") },
                visualTransformation = PasswordVisualTransformation(),
                isError = state.currentPassError != null,
                supportingText = {
                    state.currentPassError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // NUEVA CONTRASEÑA
            OutlinedTextField(
                value = state.newPass,
                onValueChange = authViewModel::onEditNewPassChange,
                label = { Text("Nueva contraseña (opcional)") },
                visualTransformation = PasswordVisualTransformation(),
                isError = state.newPassError != null,
                supportingText = {
                    state.newPassError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // CONFIRMAR CONTRASEÑA
            OutlinedTextField(
                value = state.confirmPass,
                onValueChange = authViewModel::onEditConfirmPassChange,
                label = { Text("Confirmar nueva contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = state.confirmPassError != null,
                supportingText = {
                    state.confirmPassError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // ERROR GENERAL
            state.errorMsg?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // BOTÓN GUARDAR
            Button(
                onClick = { authViewModel.submitEditProfile() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                ),
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSubmitting) "Guardando..." else "Guardar cambios")
            }

            Spacer(Modifier.height(16.dp))

            // BOTÓN VOLVER AL MENÚ
            Button(
                onClick = { navController.navigate(Route.MenuOpciones.path) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                )
            ) {
                Text("Inicio", fontSize = 25.sp)
            }
        }

        // Si se guardó bien, volvemos atrás
        LaunchedEffect(state.success) {
            if (state.success) {
                navController.popBackStack()
                authViewModel.clearEditProfileResult()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    val navController = rememberNavController()
    EditProfileScreen(navController)
}
