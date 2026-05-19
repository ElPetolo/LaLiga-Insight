package com.example.laligainsight.repository

import com.example.laligainsight.modelo.ResumenValoracion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RepositorioValoraciones {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Montamos un id estable para que cada usuario tenga una única valoración por entidad.
    private fun ratingDocId(
        entityType: String,
        entityId: String,
        userId: String
    ): String {
        return "${entityType}_${entityId}_${userId}"
    }

    // Guarda o sobrescribe la valoración del usuario actual.
    suspend fun rateEntity(
        entityType: String,
        entityId: String,
        rating: Int
    ) {

        // Si no hay usuario autenticado no tiene sentido guardar valoraciones personales.
        val userId = auth.currentUser?.uid ?: return

        val docId = ratingDocId(
            entityType,
            entityId,
            userId
        )

        val data = mapOf(
            "entityType" to entityType,
            "entityId" to entityId,
            "userId" to userId,
            "rating" to rating,
            // La fecha la genera Firestore para que todas las valoraciones sigan el mismo criterio.
            "updatedAt" to FieldValue.serverTimestamp()
        )

        db.collection("ratings")
            .document(docId)
            .set(data)
            .await()
    }

    // Calcula la media global y recupera también la valoración concreta del usuario logueado.
    suspend fun getRatingSummary(
        entityType: String,
        entityId: String
    ): ResumenValoracion {

        val userId = auth.currentUser?.uid

        val snapshot = db.collection("ratings")
            .whereEqualTo("entityType", entityType)
            .whereEqualTo("entityId", entityId)
            .get()
            .await()

        // Extraemos únicamente los números válidos para calcular la media.
        val ratings = snapshot.documents.mapNotNull {
            it.getLong("rating")?.toInt()
        }

        val average = if (ratings.isNotEmpty()) {
            ratings.average()
        } else {
            0.0
        }

        val userRating = if (userId != null) {
            // Buscamos dentro de las valoraciones cuál pertenece al usuario actual.
            snapshot.documents.firstOrNull {
                it.getString("userId") == userId
            }?.getLong("rating")?.toInt() ?: 0
        } else {
            0
        }

        return ResumenValoracion(
            average = average,
            count = ratings.size,
            userRating = userRating
        )
    }
}
