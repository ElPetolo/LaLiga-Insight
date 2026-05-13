package com.example.laligainsight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laligainsight.modelo.Scorer
import com.example.laligainsight.repository.RepositorioFutbol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel encargado de gestionar los goleadores de LaLiga
// Recoge los datos desde el Repository y los prepara para mostrarlos

class GoleadoresViewModel: ViewModel() {

    // Variables con _ : se pueden modificar (privadas)
    // Variable sin _: solo se pueden leer (públicas) --> PantallaClasificacion

    // Instancia del repositorio --> capa que actúa como intermediaria entre la API (Retrofit) y el resto de la aplicación
    private val repository = RepositorioFutbol()

    // Lista mutable de goleadores
    private val _scorers = MutableStateFlow<List<Scorer>>(emptyList())
    val scorers: StateFlow<List<Scorer>> = _scorers

    // Controla si la app está cargando los datos
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Mensaje de error en caso de fallo en API
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    // Ejecucion automática al crear el ViewModel
    init{
        loadScorers()
    }




    // Función donde cargamos los goleadores desde la API y actualiza los estados que correspondan
    fun loadScorers() {
        viewModelScope.launch {
            try {

                // Activamos el estado de carga, limpiando cualquier tipo de error previo
                _isLoading.value = true
                _error.value = null

                // Llamamos al repository para obtener los goleadores
                val response = repository.getScorers()

                // Guardamos la lista de goleadores
                _scorers.value = response.scorers

            } catch (e: Exception) {

            // Si falla, mensaje de error
                _error.value = "ERROR: No se pudieron cargar los goleadores correctamente"


            } finally {
                // Quitamos el estado de carga
                _isLoading.value = false
        }




        }
    }



}