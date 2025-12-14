package com.example.quizapp.ui.screen.auth

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
fun SetupSecurityQuestionsScreen(
    navController: NavHostController,
    userId: Long
) {
    val context = LocalContext.current
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val state by vm.setupSQ.collectAsState()

    var q1 by remember { mutableStateOf<Int?>(null) }
    var q2 by remember { mutableStateOf<Int?>(null) }
    var q3 by remember { mutableStateOf<Int?>(null) }

    var a1 by remember { mutableStateOf("") }
    var a2 by remember { mutableStateOf("") }
    var a3 by remember { mutableStateOf("") }

    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        vm.loadSecurityQuestionsForSetup()
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearSetupSecurityQuestionsResult()
            navController.navigate(Route.Perfil.path) {
                popUpTo(Route.Registro.path) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    fun validate(): Boolean {
        localError = null

        if (state.questions.size < 3) {
            localError = "No hay suficientes preguntas disponibles"
            return false
        }
        if (q1 == null || q2 == null || q3 == null) {
            localError = "Debes elegir 3 preguntas"
            return false
        }
        val ids = listOf(q1!!, q2!!, q3!!)
        if (ids.toSet().size != 3) {
            localError = "No puedes repetir preguntas"
            return false
        }
        if (a1.isBlank() || a2.isBlank() || a3.isBlank()) {
            localError = "Debes responder todas las preguntas"
            return false
        }
        return true
    }

    val isBusy = state.isLoading

    val usedIds = setOfNotNull(q1, q2, q3)

    val q1Options = state.questions.filter { it.id !in (usedIds - setOfNotNull(q1)) }
    val q2Options = state.questions.filter { it.id !in (usedIds - setOfNotNull(q2)) }
    val q3Options = state.questions.filter { it.id !in (usedIds - setOfNotNull(q3)) }

    Scaffold(topBar = { AppTopBar() }) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(16.dp)
        ) {
            Text(
                text = "Configura tus preguntas de seguridad",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(text = "Elige 3 preguntas distintas y escribe tus respuestas.")

            Spacer(Modifier.height(16.dp))

            if (state.isLoading) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
                Spacer(Modifier.height(16.dp))
            }

            state.errorMsg?.let {
                Text(it, color = Color.Red, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
            }

            localError?.let {
                Text(it, color = Color.Red, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
            }

            if (!state.isLoading && state.questions.size < 3) {
                Text(
                    text = "No hay suficientes preguntas para configurar (se requieren 3).",
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
            }

            QuestionPicker(
                label = "Pregunta 1",
                questions = q1Options,
                selectedId = q1,
                onSelected = { q1 = it; localError = null },
                enabled = !isBusy
            )
            OutlinedTextField(
                value = a1,
                onValueChange = { a1 = it; localError = null },
                label = { Text("Respuesta 1") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isBusy
            )

            Spacer(Modifier.height(12.dp))

            QuestionPicker(
                label = "Pregunta 2",
                questions = q2Options,
                selectedId = q2,
                onSelected = { q2 = it; localError = null },
                enabled = !isBusy
            )
            OutlinedTextField(
                value = a2,
                onValueChange = { a2 = it; localError = null },
                label = { Text("Respuesta 2") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isBusy
            )

            Spacer(Modifier.height(12.dp))

            QuestionPicker(
                label = "Pregunta 3",
                questions = q3Options,
                selectedId = q3,
                onSelected = { q3 = it; localError = null },
                enabled = !isBusy
            )
            OutlinedTextField(
                value = a3,
                onValueChange = { a3 = it; localError = null },
                label = { Text("Respuesta 3") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isBusy
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (!validate()) return@Button
                    vm.submitSecurityQuestionsSetup(
                        userId = userId,
                        q1 = q1!!, a1 = a1,
                        q2 = q2!!, a2 = a2,
                        q3 = q3!!, a3 = a3
                    )
                },
                enabled = !isBusy && state.questions.size >= 3,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isBusy) "Guardando..." else "Guardar", fontSize = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionPicker(
    label: String,
    questions: List<com.example.quizapp.data.remote.dto.SecurityQuestionDto>,
    selectedId: Int?,
    onSelected: (Int) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedText =
        questions.firstOrNull { it.id == selectedId }?.texto
            ?: "Selecciona una pregunta"

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded && enabled) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            questions.forEach { q ->
                DropdownMenuItem(
                    text = { Text(q.texto) },
                    onClick = {
                        onSelected(q.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
