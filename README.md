# 📱 QuizApp – Plataforma de Quizzes Gamificada

Aplicación móvil desarrollada en **Kotlin + Jetpack Compose**, integrada con microservicios en **Spring Boot**, base de datos, consumo de **API externa**, pruebas unitarias, y generación de **APK firmado**.  
Proyecto realizado para la Evaluación Parcial 4 – DSY1105 Desarrollo de Aplicaciones Móviles.

---

## 👥 **Integrantes**
- **Bastian Dias**  
- **Diego Duarte**  
- **Vicente Donoso**

---

# 🚀 1. Descripción general del proyecto

**QuizApp** es una aplicación móvil que permite a los usuarios registrarse, iniciar sesión, jugar quizzes por categorías, guardar su progreso, visualizar récords, enviar feedback y gestionar contenido (modo administrador).

La app se conecta mediante **microservicios propios**, integrando además una **API externa real** para datos adicionales dentro del flujo visual.

---

# 📱 2. Funcionalidades principales

### 🧑‍💻 Usuario
- Registro de cuenta  
- Inicio de sesión  
- Edición de perfil  
- Selección de categorías  
- Resolver quizzes  
- Ver resultados y puntajes  
- Enviar feedback  

### 🛠️ Administrador
- Gestión de preguntas  
- Gestión de jugadores  
- Gestión de feedback  
- Historial de partidas  
- Panel administrativo completo  

### 🔗 Otras características
- Consumo de API externa (hechos curiosos/random facts)  
- Validaciones visuales  
- Navegación completa con Jetpack Compose Navigation  
- Manejo de estados con ViewModels  
- Soporte para modo offline parcial (caching UI en estados)  

---

# 🏗️ 3. Arquitectura del proyecto

El ecosistema completo está compuesto por:

### 📱 **1. Aplicación móvil (Kotlin + Jetpack Compose)**
- MVVM  
- Retrofit  
- ViewModel + Estado inmutable  
- Pantallas modulares  
- Pruebas unitarias + Coverage  

### 🖥️ **2. Microservicios (Spring Boot + Maven)**
Incluye:
- `microservice-gateway`
- `microservice-auth-service`
- `microservice-quiz`
- `microservice-feedback`
- `microservice-game`

Cada microservicio contiene:
- Controladores  
- Servicios  
- Repositorios  
- Entidades  
- DTOs  
- Interfaces REST

### 🌐 **3. API Externa**
Usamos la API pública:  
`https://uselessfacts.jsph.pl/random.json?language=es`

Consumida con Retrofit desde la app móvil.

---

# 🌍 4. Endpoints utilizados

## 🔐 **Auth Service**
- `POST /api/auth/register`  
- `POST /api/auth/login`  
- `GET /api/auth/usuarios`  
- `PUT /api/auth/usuarios/{id}`  

## ❓ **Quiz Service**
- `GET /api/quiz/preguntas`  
- `POST /api/quiz/preguntas`  
- `PUT /api/quiz/preguntas/{id}`  
- `DELETE /api/quiz/preguntas/{id}`  

## 🎮 **Game Service**
- `POST /api/game/partida`  
- `GET /api/game/historial`  

## 💬 **Feedback Service**
- `POST /api/feedback`  
- `GET /api/feedback`  

## 🌐 **API Externa (Retrofit)**
`GET https://uselessfacts.jsph.pl/random.json?language=es`

---

# 🧪 5. Pruebas unitarias

Se implementaron pruebas unitarias en:

### 📱 App móvil
- `AuthViewModel`
- `MenuOpcionesViewModel`
- `QuizViewModel`
- `FeedbackViewModel`
- Entre otros…

Incluyendo:
- `JUnit`
- `MockK`
- `Turbine`
- `MainDispatcherRule`
- Cobertura habilitada en `build.gradle.kts`

### 🖥️ Microservicios
Pruebas completas en:
- Controladores  
- Servicios  
- Repositorios  

📌 **Cobertura objetivo:** ≥ **80%** de lógica (según pauta).

---

# 📦 6. Generación del APK firmado

El APK se generó mediante:
Build > Generate Signed Bundle/APK


Incluye:
- Archivo: `QuizApp.apk`  
- Llave `.jks` incluida en el repositorio o documentada en `/docs/llave/`  
- Configuración *signing* instalada en Android Studio

### 📁 Archivos entregados:
- `/app-release/QuizApp.apk`
- `/docs/llave/` (capturas de la llave o archivo comprimido)

---

# 🧭 7. Pasos para ejecutar el proyecto

---

## 📱 **A. Ejecutar la Aplicación Móvil**

1. Abrir Android Studio  
2. Importar el proyecto `QuizApp`  
3. Verificar que la URL base está configurada:

```kotlin
const val BASE_URL = "http://10.0.2.2:8080/"
```
Ejecutar los microservicios
1. Config
2. Eureka
3. GateWay
4. Auth
5. Quiz
6. Game
7. Feedback

Levantar la app con un emulador o un dispositivo físico

La app se conectará automáticamente al gateway

Base de datos

Dependiendo del proyecto:

H2 en memoria

o MySQL/PostgreSQL configurado en application.properties
