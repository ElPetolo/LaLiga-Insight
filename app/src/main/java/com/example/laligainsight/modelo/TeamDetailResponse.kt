package com.example.laligainsight.modelo

data class TeamDetailResponse(
    val squad: List<Player>
)

data class Player(
    val id: Int,
    val name: String,
    val position: String?,
    val dateOfBirth: String?,
    val nationality: String?,
    val shirtNumber: Int?
)