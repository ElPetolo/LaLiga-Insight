package com.example.laligainsight.Auth

import com.example.laligainsight.modelo.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun getUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(User::class.java)
    }

    suspend fun createUserIfNotExists(email: String) {
        val uid = auth.currentUser?.uid ?: return
        val username = email.substringBefore("@")

        val docRef = db.collection("users").document(uid)
        val doc = docRef.get().await()


        if (!doc.exists()) {
            val user = User(
                uid = uid,
                email = email,
                username = username,
                usernameLowercase = username.lowercase(),
                favoriteTeam = "",
                favoriteTeamCrest = "",
                profileImageUrl = "",
                friends = emptyList(),
                sentRequests = emptyList(),
                receivedRequests = emptyList()
            )

            docRef.set(user).await()
        } else {
            val updates = mutableMapOf<String, Any>()

            if (!doc.contains("usernameLowercase")) {
                updates["usernameLowercase"] = username.lowercase()
            }

            if (!doc.contains("favoriteTeamCrest")) {
                updates["favoriteTeamCrest"] = ""
            }

            if (!doc.contains("profileImageUrl")) {
                updates["profileImageUrl"] = ""
            }

            if (!doc.contains("friends")) {
                updates["friends"] = emptyList<String>()
            }

            if (!doc.contains("sentRequests")) {
                updates["sentRequests"] = emptyList<String>()
            }

            if (!doc.contains("receivedRequests")) {
                updates["receivedRequests"] = emptyList<String>()
            }

            if (updates.isNotEmpty()) {
                docRef.update(updates).await()
            }
        }
    }

    suspend fun updateUsername(newUsername: String) {
        val uid = auth.currentUser?.uid ?: return
        val cleanUsername = newUsername.trim()

        if (!isUsernameAvailable(cleanUsername)) {
            throw Exception("Username ya en uso")
        }

        db.collection("users").document(uid)
            .update(
                mapOf(
                    "username" to cleanUsername,
                    "usernameLowercase" to cleanUsername.lowercase()
                )
            )
            .await()
    }

    suspend fun updateFavoriteTeam(teamName: String, teamCrest: String) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .update(
                mapOf(
                    "favoriteTeam" to teamName,
                    "favoriteTeamCrest" to teamCrest
                )
            )
            .await()
    }

    suspend fun updateProfileImage(imageUrl: String) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .update("profileImageUrl", imageUrl)
            .await()
    }

    suspend fun searchUsersByUsername(query: String): List<User> {
        val currentUid = auth.currentUser?.uid ?: return emptyList()
        val cleanQuery = query.trim().lowercase()

        if (cleanQuery.isBlank()) return emptyList()

        val snapshot = db.collection("users")
            .whereGreaterThanOrEqualTo("usernameLowercase", cleanQuery)
            .whereLessThanOrEqualTo("usernameLowercase", cleanQuery + "\uf8ff")
            .limit(10)
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { it.toObject(User::class.java) }
            .filter { it.uid != currentUid }
    }

    suspend fun sendFriendRequest(friendUid: String) {
        val currentUid = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUid)
            .update("sentRequests", FieldValue.arrayUnion(friendUid))
            .await()

        db.collection("users").document(friendUid)
            .update("receivedRequests", FieldValue.arrayUnion(currentUid))
            .await()
    }

    suspend fun acceptFriendRequest(friendUid: String) {
        val currentUid = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUid)
            .update(
                mapOf(
                    "friends" to FieldValue.arrayUnion(friendUid),
                    "receivedRequests" to FieldValue.arrayRemove(friendUid)
                )
            )
            .await()

        db.collection("users").document(friendUid)
            .update(
                mapOf(
                    "friends" to FieldValue.arrayUnion(currentUid),
                    "sentRequests" to FieldValue.arrayRemove(currentUid)
                )
            )
            .await()
    }

    suspend fun rejectFriendRequest(friendUid: String) {
        val currentUid = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUid)
            .update("receivedRequests", FieldValue.arrayRemove(friendUid))
            .await()

        db.collection("users").document(friendUid)
            .update("sentRequests", FieldValue.arrayRemove(currentUid))
            .await()
    }

    suspend fun getFriends(): List<User> {
        val currentUser = getUser() ?: return emptyList()

        if (currentUser.friends.isEmpty()) return emptyList()

        val friends = mutableListOf<User>()

        currentUser.friends.forEach { friendUid ->
            val doc = db.collection("users").document(friendUid).get().await()
            doc.toObject(User::class.java)?.let {
                friends.add(it)
            }
        }

        return friends
    }

    suspend fun uploadProfileImage(uri: Uri): String {

        val uid = auth.currentUser?.uid ?: return ""

        val storageRef = FirebaseStorage.getInstance()

            .reference

            .child("profile_images/$uid.jpg")

        storageRef.putFile(uri).await()

        return storageRef.downloadUrl.await().toString()

    }

    suspend fun getReceivedRequests(): List<User> {
        val currentUser = getUser() ?: return emptyList()

        if (currentUser.receivedRequests.isEmpty()) return emptyList()

        val requests = mutableListOf<User>()

        currentUser.receivedRequests.forEach { userUid ->
            val doc = db.collection("users").document(userUid).get().await()
            doc.toObject(User::class.java)?.let {
                requests.add(it)
            }
        }

        return requests
    }

    suspend fun getSentRequests(): List<User> {
        val currentUser = getUser() ?: return emptyList()

        if (currentUser.sentRequests.isEmpty()) return emptyList()

        val requests = mutableListOf<User>()

        currentUser.sentRequests.forEach { userUid ->
            val doc = db.collection("users").document(userUid).get().await()
            doc.toObject(User::class.java)?.let {
                requests.add(it)
            }
        }

        return requests
    }

    suspend fun removeFriend(friendUid: String) {
        val currentUid = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUid)
            .update("friends", FieldValue.arrayRemove(friendUid))
            .await()

        db.collection("users").document(friendUid)
            .update("friends", FieldValue.arrayRemove(currentUid))
            .await()
    }

    suspend fun getUserById(uid: String): User? {
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(User::class.java)
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        val clean = username.trim().lowercase()

        val snapshot = db.collection("users")
            .whereEqualTo("usernameLowercase", clean)
            .get()
            .await()

        return snapshot.isEmpty
    }

}