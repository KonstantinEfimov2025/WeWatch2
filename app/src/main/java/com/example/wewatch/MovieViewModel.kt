package com.example.wewatch

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    val localMovies: Flow<List<Movie>> = repository.getAllMovies()

    private val _searchResults = mutableStateListOf<Movie>()
    val searchResults: List<Movie> = _searchResults

    var selectedMovie = mutableStateOf<Movie?>(null)
        private set

    fun searchMovies(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            try {
                Log.d("DEBUG_WATCH", "Searching for: $query")
                val response = RetrofitClient.instance.searchMovies(query)

                _searchResults.clear()
                if (response.searchResults != null) {
                    _searchResults.addAll(response.searchResults)
                }
            } catch (e: Exception) {
                Log.e("DEBUG_WATCH", "Search error: ${e.message}")
            }
        }
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            repository.insert(movie)
        }
    }

    fun deleteSelectedMovies() {
        viewModelScope.launch {
            repository.deleteSelected()
        }
    }

    // ИСПРАВЛЕНО: Теперь обновляем состояние в базе данных
    fun updateMovieCheck(movie: Movie, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedMovie = movie.copy(isChecked = isChecked)
            repository.update(updatedMovie)
        }
    }

    fun selectMovie(movie: Movie) {
        selectedMovie.value = movie
    }
}

class MovieRepository(private val movieDao: MovieDao) {
    fun getAllMovies() = movieDao.getAllMovies()
    suspend fun insert(movie: Movie) = movieDao.insertMovie(movie)
    suspend fun deleteSelected() = movieDao.deleteSelectedMovies()
    // Добавлено для связи с DAO
    suspend fun update(movie: Movie) = movieDao.updateMovie(movie)
}

class MovieViewModelFactory(private val dao: MovieDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(MovieRepository(dao)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}