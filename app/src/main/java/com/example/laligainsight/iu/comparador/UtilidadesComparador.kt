package com.example.laligainsight.iu

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Locale

// Esta división evita dejar helpers mezclados con composables grandes.
fun safeDivide(value: Int, total: Int): Double {
    return if (total > 0) value.toDouble() / total.toDouble() else 0.0
}

// Formateamos con dos decimales para que la comparativa se vea uniforme.
fun formatDecimal(value: Double): String {
    return String.format(Locale.US, "%.2f", value)
}

// Esta consulta sigue siendo la misma; solo la dejamos en un archivo util de la feature.
suspend fun getPlayerImageUrlFromFirebase(
    playerName: String,
    teamName: String
): String {
    val db = FirebaseFirestore.getInstance()

    val snapshot = db.collection("player_images")
        .whereEqualTo("playerName", playerName)
        .whereEqualTo("teamName", teamName)
        .limit(1)
        .get()
        .await()

    return snapshot.documents.firstOrNull()
        ?.getString("imageUrl")
        ?: ""
}
