package com.example.laligainsight.repository

import com.example.laligainsight.modelo.RatingSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RatingRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun ratingDocId(
        entityType: String,
        entityId: String,
        userId: String
    ): String {
        return "${entityType}_${entityId}_${userId}"
    }

    suspend fun rateEntity(
        entityType: String,
        entityId: String,
        rating: Int
    ) {

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
            "updatedAt" to FieldValue.serverTimestamp()
        )

        db.collection("ratings")
            .document(docId)
            .set(data)
            .await()
    }

    suspend fun getRatingSummary(
        entityType: String,
        entityId: String
    ): RatingSummary {

        val userId = auth.currentUser?.uid

        val snapshot = db.collection("ratings")
            .whereEqualTo("entityType", entityType)
            .whereEqualTo("entityId", entityId)
            .get()
            .await()

        val ratings = snapshot.documents.mapNotNull {
            it.getLong("rating")?.toInt()
        }

        val average = if (ratings.isNotEmpty()) {
            ratings.average()
        } else {
            0.0
        }

        val userRating = if (userId != null) {
            snapshot.documents.firstOrNull {
                it.getString("userId") == userId
            }?.getLong("rating")?.toInt() ?: 0
        } else {
            0
        }

        return RatingSummary(
            average = average,
            count = ratings.size,
            userRating = userRating
        )
    }
}