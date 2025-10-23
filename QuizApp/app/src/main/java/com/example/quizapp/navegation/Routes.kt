package com.example.quizapp.navegation

sealed class Route(val path: String) {
    data object Login : Route("login")
    data object MenuInicioSesion : Route("menuiniciosesion")
    data object MenuOpciones : Route("menuopciones")
    data object Perfil : Route("perfil")
    data object Registro : Route("registro")
    data object Selecion : Route("selecion")
    data object CategoriaFacil : Route("Facil")
    data object CategoriaNormal : Route("Normal")
    data object CategoriaDificil : Route("Dificil")
    data object QuizFacil : Route("QuizFacil")
    data object QuizNormal : Route("QuizNormal")
    data object QuizDificil : Route("QuizDificil")

}
