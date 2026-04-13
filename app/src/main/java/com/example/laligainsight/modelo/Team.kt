package com.example.laligainsight.modelo

import com.google.gson.annotations.SerializedName

// Completamos la data class Team, añadiendo las variables id y crest (escudo)
data class Team(
    val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("crest") val crest: String,
    val venue: String? = null
)


