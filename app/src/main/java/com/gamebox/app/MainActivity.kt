package com.gamebox.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GameBoxApp() }
    }
}

private val Bg = Color(0xFF121212)
private val CardTop = Color(0xFF3A3F5C)
private val CardBottom = Color(0xFF1B1D2B)
private val Accent = Color(0xFF7C4DFF)

private val games = listOf("Game 1", "Game 2", "Game 3", "Game 4", "Game 5", "Game 6")

@Composable
fun GameBoxApp() {
    val tabs = listOf("Home", "Library", "Settings")
    var tab by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = selected != null) { selected = null }

    MaterialTheme(
        colorScheme = darkColorScheme(primary = Accent, background = Bg, surface = Bg)
    ) {
        Scaffold(
            containerColor = Bg,
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF1B1B1F)) {
                    tabs.forEachIndexed { i, label ->
                        NavigationBarItem(
                            selected = tab == i,
                            onClick = { tab = i; selected = null },
                            icon = { Text(label.take(1), fontWeight = FontWeight.Bold) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).padding(horizontal = 16.dp)) {
                val game = selected
                when {
                    game != null -> DetailScreen(game, onBack = { selected = null })
                    tab == 0 -> HomeScreen { selected = it }
                    tab == 1 -> LibraryScreen { selected = it }
                    else -> SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun ScreenTitle(text: String) {
    Text(
        text,
        color = Color.White,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 20.dp)
    )
}

@Composable
fun HomeScreen(onOpen: (String) -> Unit) {
    Column {
        ScreenTitle("GAMEBOX")
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(games) { name -> GameCard(name) { onOpen(name) } }
        }
    }
}

@Composable
fun LibraryScreen(onOpen: (String) -> Unit) {
    Column {
        ScreenTitle("Library")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(games) { name ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBottom)
                        .clickable { onOpen(name) }
                        .padding(20.dp)
                ) {
                    Text(name, color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    Column {
        ScreenTitle("Settings")
        Text("Coming soon", color = Color.Gray)
    }
}

@Composable
fun DetailScreen(name: String, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize()) {
        TextButton(onClick = onBack) { Text("< Back") }
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(CardTop, CardBottom)))
        )
        Text(
            name,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Button(
            onClick = {
                Toast.makeText(context, "Play: $name", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Play", fontSize = 18.sp)
        }
    }
}

@Composable
fun GameCard(name: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(CardTop, CardBottom)))
            .clickable(onClick = onClick)
    ) {
        Text(
            name,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
        )
    }
}
