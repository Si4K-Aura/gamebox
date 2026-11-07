package com.gamebox.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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

data class Game(val name: String, val uri: String)

private val Bg = Color(0xFF121212)
private val CardTop = Color(0xFF3A3F5C)
private val CardBottom = Color(0xFF1B1D2B)
private val Accent = Color(0xFF7C4DFF)

fun loadGames(prefs: SharedPreferences): List<Game> =
    (prefs.getString("games", "") ?: "")
        .lines()
        .filter { it.contains("\t") }
        .map { line ->
            val p = line.split("\t")
            Game(p[0], p[1])
        }

fun saveGames(prefs: SharedPreferences, games: List<Game>) {
    val text = games.joinToString("\n") { "${it.name}\t${it.uri}" }
    prefs.edit().putString("games", text).apply()
}

fun displayName(context: Context, uri: Uri): String {
    context.contentResolver.query(uri, null, null, null, null)?.use { c ->
        val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (i >= 0 && c.moveToFirst()) return c.getString(i)
    }
    return "Game"
}

@Composable
fun GameBoxApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("gamebox", Context.MODE_PRIVATE) }
    var games by remember { mutableStateOf(loadGames(prefs)) }
    var selected by remember { mutableStateOf<Game?>(null) }
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Library", "Settings")

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
            }
            val name = displayName(context, uri).substringBeforeLast('.')
            games = games + Game(name, uri.toString())
            saveGames(prefs, games)
        }
    }

    BackHandler(enabled = selected != null) { selected = null }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            onPrimary = Color.White,
            background = Bg,
            surface = Bg
        )
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
                    game != null -> DetailScreen(
                        game,
                        onBack = { selected = null },
                        onRemove = {
                            games = games - game
                            saveGames(prefs, games)
                            selected = null
                        }
                    )
                    tab == 0 -> HomeScreen(games) { selected = it }
                    tab == 1 -> LibraryScreen(
                        games,
                        onAdd = { picker.launch(arrayOf("*/*")) }
                    ) { selected = it }
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
fun EmptyHint() {
    Text("No games yet. Open Library and tap Add game.", color = Color.Gray)
}

@Composable
fun HomeScreen(games: List<Game>, onOpen: (Game) -> Unit) {
    Column {
        ScreenTitle("LAUNCHER")
        if (games.isEmpty()) {
            EmptyHint()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(games) { game -> GameCard(game.name) { onOpen(game) } }
            }
        }
    }
}

@Composable
fun LibraryScreen(games: List<Game>, onAdd: () -> Unit, onOpen: (Game) -> Unit) {
    Column {
        ScreenTitle("Library")
        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("+ Add game")
        }
        Spacer(Modifier.height(12.dp))
        if (games.isEmpty()) {
            EmptyHint()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(games) { game ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBottom)
                            .clickable { onOpen(game) }
                            .padding(20.dp)
                    ) {
                        Text(game.name, color = Color.White, fontSize = 18.sp)
                    }
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
fun DetailScreen(game: Game, onBack: () -> Unit, onRemove: () -> Unit) {
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
            game.name,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Button(
            onClick = {
                Toast.makeText(context, "Play: ${game.name}", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Play", fontSize = 18.sp)
        }
        TextButton(onClick = onRemove, modifier = Modifier.padding(top = 8.dp)) {
            Text("Remove from library")
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
