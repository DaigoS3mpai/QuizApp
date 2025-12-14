package com.example.quizapp.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String = "Retro Flash",
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    windowSizeClass: WindowSizeClass? = null
) {
    // Tamaño de texto según ancho de pantalla
    val titleTextStyle = when (windowSizeClass?.widthSizeClass) {
        WindowWidthSizeClass.Compact -> MaterialTheme.typography.titleMedium   // celulares chicos
        WindowWidthSizeClass.Medium -> MaterialTheme.typography.titleLarge    // celulares grandes
        WindowWidthSizeClass.Expanded -> MaterialTheme.typography.headlineSmall // tablets
        else -> MaterialTheme.typography.titleLarge
    }

    // Altura de la AppBar según tamaño
    val appBarHeight = when (windowSizeClass?.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 56.dp
        WindowWidthSizeClass.Medium -> 64.dp
        WindowWidthSizeClass.Expanded -> 72.dp
        else -> 64.dp
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                color = Color.Black,
                style = titleTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (showBackButton && onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }
            }
        },
        actions = { actions?.invoke() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF87CEEB),
            titleContentColor = Color.Black
        ),
        modifier = Modifier
            .height(appBarHeight)
    )
}
