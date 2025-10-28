package com.example.quizapp.navegation

sealed class Route(val path: String) {
    // 🔹 Pantallas base
    data object Login : Route("login")
    data object MenuInicioSesion : Route("menuiniciosesion")
    data object MenuOpciones : Route("menuopciones")
    data object Perfil : Route("perfil")
    data object Registro : Route("registro")
    data object Selecion : Route("selecion")
    data object Password : Route("password")
    // Pantalla de selección de categoría (recibe el nivel de dificultad)
    data object CategoriaScreen : Route("Categoria/{dificultadId}")

    // Pantalla de quiz (recibe dificultad y categoría)
    data object QuizScreen : Route("QuizFacil/{dificultadId}/{categoriaId}")
}
