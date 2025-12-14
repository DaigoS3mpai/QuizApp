package com.example.quizapp.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityQuestionsVerify(
    navController: NavHostController,
    email: String
) {
    val context = LocalContext.current
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val rState by vm.recovery.collectAsState()

    var answer1 by remember { mutableStateOf("") }
    var answer2 by remember { mutableStateOf("") }
    var answer3 by remember { mutableStateOf("") }

    var localErrorMsg by remember { mutableStateOf<String?>(null) }
    var buttonsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(email) {
        buttonsVisible = true
        vm.loadRecoveryQuestions(email)
    }

    val q1 = rState.questions.getOrNull(0)
    val q2 = rState.questions.getOrNull(1)
    val q3 = rState.questions.getOrNull(2)

    val q1Text = q1?.texto ?: "Pregunta 1"
    val q2Text = q2?.texto ?: "Pregunta 2"
    val q3Text = q3?.texto ?: "Pregunta 3"

    Scaffold(
        topBar = { AppTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Responde tus preguntas de seguridad",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (rState.isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
                Spacer(Modifier.height(16.dp))
            }

            rState.errorMsg?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = answer1,
                onValueChange = { answer1 = it; localErrorMsg = null },
                label = { Text(q1Text) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !rState.isLoading
            )

            OutlinedTextField(
                value = answer2,
                onValueChange = { answer2 = it; localErrorMsg = null },
                label = { Text(q2Text) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !rState.isLoading
            )

            OutlinedTextField(
                value = answer3,
                onValueChange = { answer3 = it; localErrorMsg = null },
                label = { Text(q3Text) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !rState.isLoading
            )

            AnimatedVisibility(
                visible = buttonsVisible,
                enter = scaleIn(animationSpec = tween(500)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
                Column {
                    Button(
                        onClick = {
                            if (rState.questions.size < 3 || q1 == null || q2 == null || q3 == null) {
                                localErrorMsg = "No hay suficientes preguntas para verificar"
                                return@Button
                            }
                            if (answer1.isBlank() || answer2.isBlank() || answer3.isBlank()) {
                                localErrorMsg = "Debes responder todas las preguntas"
                                return@Button
                            }

                            val items = listOf(
                                q1.id to answer1,
                                q2.id to answer2,
                                q3.id to answer3
                            )

                            vm.verifyRecoveryAnswers(
                                items = items,
                                onOk = { token ->
                                    navController.navigate(Route.Password.createRoute(email, token))
                                },
                                onFail = { msg ->
                                    localErrorMsg = msg
                                }
                            )
                        },
                        enabled = !rState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF58B956),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Verificar", fontSize = 18.sp)
                    }

                    localErrorMsg?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
