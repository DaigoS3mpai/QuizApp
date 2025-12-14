package com.example.quizapp.navegation

sealed class Route(val path: String) {

    //Pantallas base
    data object Login : Route("login")
    data object MenuInicioSesion : Route("menuiniciosesion")
    data object MenuOpciones : Route("menuopciones")
    data object Perfil : Route("perfil")
    data object Registro : Route("registro")
    data object Selecion : Route("selecion")
    data object ForgotPasswordEmail : Route("forgot_password_email")

    data object VerifySecurityQuestions : Route("verify_security_questions/{email}") {
        fun createRoute(email: String): String = "verify_security_questions/$email"
    }

    data object Password : Route("password/{email}/{token}") {
        fun createRoute(email: String, token: String): String =
            "password/$email/$token"
    }

    data object SetupSecurityQuestions : Route("setup_security_questions/{userId}") {
        fun createRoute(userId: Long): String = "setup_security_questions/$userId"
    }



    data object EditProfile : Route("editprofile")

    //Pantalla de categorías (recibe id dificultad)
    data object CategoriaScreen : Route("Categoria/{dificultadId}") {
        fun createRoute(dificultadId: Long): String =
            "Categoria/$dificultadId"
    }

    //Pantalla de quiz (recibe dificultad + categoría)
    data object QuizScreen : Route("QuizScreen/{dificultadId}/{categoriaId}") {
        fun createRoute(dificultadId: Long, categoriaId: Long): String =
            "QuizScreen/$dificultadId/$categoriaId"
    }

    //Pantalla para mis partidas
    object MisPartidas : Route("mis_partidas")

    //Pantallas admin
    data object AdminMenu : Route("adminmenu")
    data object AdminJugadores : Route("adminjugadores")
    data object AdminHistorial : Route("adminhistorial")
    data object AdminPreguntas : Route("adminpreguntas")
    data object AdminFeedback : Route("adminfeedback")
    data object FeedbackJugador : Route("feedbackJugador")
    data object QuizMenu : Route("quizmenu")
}
