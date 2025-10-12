package com.example.quizapp.navegation

sealed class Route(val path: String) {
    data object Categoria : Route("categoria")
    data object Login : Route("login")
    data object MenuInicioSesion : Route("menuiniciosesion")
    data object MenuOpciones : Route("menuopciones")
    data object Perfil : Route("perfil")
    data object Registro : Route("registro")
    data object Selecion : Route("selecion")
}
