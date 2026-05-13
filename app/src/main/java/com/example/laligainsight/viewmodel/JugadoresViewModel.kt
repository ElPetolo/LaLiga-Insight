package com.example.laligainsight.viewmodel

import androidx.lifecycle.ViewModel
import com.example.laligainsight.modelo.JugadorFirebase
import com.example.laligainsight.modelo.Player
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel para obtener jugadores desde Firestore

class JugadoresViewModel() : ViewModel() {

    // Referencia a Firestore
    private val db = FirebaseFirestore.getInstance()

    // Lista privada mutable de jugadores
    private val _players = MutableStateFlow<List<JugadorFirebase>>(emptyList())

    // Lista pública de jugadores
    val players: StateFlow<List<JugadorFirebase>> = _players

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Inicialización del ViewModel
    init {
        loadPlayers()
    }

    // Función para cargar jugadores desde Firestore
    fun loadPlayers() {

        // Si esta cargando...
        _isLoading.value = true

        db.collection("player_images")
            .get()
            .addOnSuccessListener { result ->

                // Convertimos cada documento en JugadorFirebase
                val playerList = result.map { document ->
                    JugadorFirebase(
                        imageUrl = document.getString("imageUrl") ?: "",
                        playerName = document.getString("playerName") ?: "",
                        teamName = document.getString("teamName") ?: ""
                    )
                }

                // Actualizamos la lista de jugadores
                _players.value = playerList

                // Quitamos el estado de carga
                _isLoading.value = false
            }

            .addOnFailureListener {
                // Si falla, mostramos mensaje de error
                _error.value = "ERROR: No se pudieron cargar los jugadores correctamente"

                // Quitamos el estado de carga
                _isLoading.value = false
            }
    }
}