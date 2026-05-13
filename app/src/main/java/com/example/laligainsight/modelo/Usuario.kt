package com.example.laligainsight.modelo

// Modelo de datos para el usuario de la APP
data class Usuario(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val usernameLowercase: String = "",
    val favoriteTeam: String = "",
    val favoriteTeamCrest: String = "",
    val profileImageUrl: String = "",
    val friends: List<String> = emptyList(),
    val sentRequests: List<String> = emptyList(),
    val receivedRequests: List<String> = emptyList()
)