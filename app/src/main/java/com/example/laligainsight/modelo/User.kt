package com.example.laligainsight.modelo

data class User(
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