package com.example.wewatch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MovieViewModel, onAddClick: () -> Unit) {
    // Список выбранных фильмов хранится в локальной базе данных Room
    val movies by viewModel.localMovies.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            // В основном меню должна быть кнопка для удаления отмеченных фильмов
            TopAppBar(
                title = { Text("Movies to Watch") },
                actions = {
                    IconButton(onClick = { viewModel.deleteSelectedMovies() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                    }
                }
            )
        },
        floatingActionButton = {
            // Кнопка FAB для перехода к экрану добавления
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Movie")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (movies.isEmpty()) {
                // До выбора фильмов отображается рисунок пустого кадра и текст
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_report_image),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("There are currently no movies in your watch list.")
                }
            } else {
                // Список выбранных фильмов
                LazyColumn {
                    items(movies) { movie ->
                        MovieItem(movie = movie) { isChecked ->
                            viewModel.updateMovieCheck(movie, isChecked)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onCheckedChange: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Каждый элемент содержит постер, название, год и флажок
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = movie.poster,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
                Text(text = movie.year, style = MaterialTheme.typography.bodySmall)
            }
            Checkbox(
                checked = movie.isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}