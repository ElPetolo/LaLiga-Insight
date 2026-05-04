package com.example.laligainsight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laligainsight.modelo.StandingTeam
import com.example.laligainsight.repository.FootballRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel encargado de gestionar la clasificación de LaLiga
// Recoge los datos desde el Repository y los prepara para mostrarlos
class StandingsViewModel: ViewModel() {


    // Variables con _ : se pueden modificar (privadas)
    // Variable sin _: solo se pueden leer (públicas) --> StandingsScreen

    private val repository = FootballRepository()

    // MutableStateFlow: "Caja que guarda datos y avisa cuando cambian"
    private val _totalStandings = MutableStateFlow<List<StandingTeam>>(emptyList())
    val totalStandings: StateFlow<List<StandingTeam>> = _totalStandings


    // Variable que indica si se está cargando la clasificación
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Variable para indicar si hay error en la API
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Cuando se crea el ViewModel, se carga la clasificación
    init {
        loadStandings()
    }


    // Funcion que carga la clasificacion desde la API
    // Se separan los datos según el tipo de clasificación: TOTAL, HOME, AWAT
    fun loadStandings() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val response = repository.getStandings()

                // Comprobamos que trae las 3 tablas
                println("TIPOS DE CLASIFICACIÓN: ${response.standings.map { it.type }}")

                // Tabla general
                val table = response.standings
                    .firstOrNull() {it.type == "TOTAL" }
                    ?.table
                    ?: emptyList()

                // Guardamos la clasificación para que la tabla la pueda mostrar
                _totalStandings.value = table

            } catch (e: Exception) {
                _error.value = "ERROR: No se pudo cargar la clasificación correctamente"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

