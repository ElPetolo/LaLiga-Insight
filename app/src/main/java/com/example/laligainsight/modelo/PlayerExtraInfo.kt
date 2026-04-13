package com.example.laligainsight.modelo

data class PlayerExtraInfo(
    val fullName: String,
    val position: String,
    val birthday: String,
    val nationality: String,
    val currentTeam: String,
    val contract: String,
    val marketValue: String,
    val preferredFoot: String,
    val matchesInDatabase: String,
    val relatedItems: List<String>
)