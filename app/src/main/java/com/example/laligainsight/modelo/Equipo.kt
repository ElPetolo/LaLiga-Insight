package com.example.laligainsight.modelo

import com.google.gson.annotations.SerializedName

// Completamos la data class Equipo, añadiendo las variables id y crest (escudo)
data class Equipo(
    val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("crest") val crest: String,
    val venue: String? = null
)


