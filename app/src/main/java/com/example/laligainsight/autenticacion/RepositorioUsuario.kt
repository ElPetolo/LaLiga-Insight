package com.example.laligainsight.autenticacion

import com.example.laligainsight.modelo.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri

class RepositorioUsuario {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Carga el documento del usuario autenticado y lo convierte al modelo de la app.
    suspend fun getUser(): Usuario? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(Usuario::class.java)
    }

    // Si el usuario entra por primera vez, le creamos su documento base en Firestore.
    // Si ya existe, aprovechamos para rellenar campos nuevos que puedan faltar.
    suspend fun createUserIfNotExists(email: String) {
        val uid = auth.currentUser?.uid ?: return
        val username = email.substringBefore("@")

        val docRef = db.collection("users").document(uid)
        val doc = docRef.get().await()


        if (!doc.exists()) {
            val user = Usuario(
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

    // Actualiza el username y guarda también su versión en minúsculas para búsquedas.
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

    // Guarda el equipo favorito junto al escudo para poder pintarlo luego sin otra consulta.
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

    // Cambia la URL de la foto de perfil en el documento del usuario.
    suspend fun updateProfileImage(imageUrl: String) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .update("profileImageUrl", imageUrl)
            .await()
    }

    // Busca usuarios por prefijo de username y excluye al usuario actual de los resultados.
    suspend fun searchUsersByUsername(query: String): List<Usuario> {
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
            .mapNotNull { it.toObject(Usuario::class.java) }
            .filter { it.uid != currentUid }
    }

    // Añade la solicitud en ambos lados para que cada usuario vea el estado que le toca.
    suspend fun sendFriendRequest(friendUid: String) {
        val currentUid = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUid)
            .update("sentRequests", FieldValue.arrayUnion(friendUid))
            .await()

        db.collection("users").document(friendUid)
            .update("receivedRequests", FieldValue.arrayUnion(currentUid))
            .await()
    }

    // Al aceptar, la petición desaparece y ambos usuarios pasan a ser amigos.
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

    // Rechazar solo limpia la solicitud pendiente en ambos documentos.
    suspend fun rejectFriendRequest(friendUid: String) {
        val currentUid = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUid)
            .update("receivedRequests", FieldValue.arrayRemove(friendUid))
            .await()

        db.collection("users").document(friendUid)
            .update("sentRequests", FieldValue.arrayRemove(currentUid))
            .await()
    }

    // Resuelve la lista de ids de amigos a objetos completos de tipo Usuario.
    suspend fun getFriends(): List<Usuario> {
        val currentUser = getUser() ?: return emptyList()

        if (currentUser.friends.isEmpty()) return emptyList()

        val friends = mutableListOf<Usuario>()

        currentUser.friends.forEach { friendUid ->
            val doc = db.collection("users").document(friendUid).get().await()
            doc.toObject(Usuario::class.java)?.let {
                friends.add(it)
            }
        }

        return friends
    }

    // Sube la imagen al storage del usuario y devuelve la URL pública para guardarla en Firestore.
    suspend fun uploadProfileImage(uri: Uri): String {

        val uid = auth.currentUser?.uid ?: return ""

        val storageRef = FirebaseStorage.getInstance()

            .reference

            .child("profile_images/$uid.jpg")

        storageRef.putFile(uri).await()

        return storageRef.downloadUrl.await().toString()

    }

    // Convierte las solicitudes recibidas en una lista de usuarios lista para la UI.
    suspend fun getReceivedRequests(): List<Usuario> {
        val currentUser = getUser() ?: return emptyList()

        if (currentUser.receivedRequests.isEmpty()) return emptyList()

        val requests = mutableListOf<Usuario>()

        currentUser.receivedRequests.forEach { userUid ->
            val doc = db.collection("users").document(userUid).get().await()
            doc.toObject(Usuario::class.java)?.let {
                requests.add(it)
            }
        }

        return requests
    }

    // Igual que las recibidas, pero con las que el usuario ya ha enviado.
    suspend fun getSentRequests(): List<Usuario> {
        val currentUser = getUser() ?: return emptyList()

        if (currentUser.sentRequests.isEmpty()) return emptyList()

        val requests = mutableListOf<Usuario>()

        currentUser.sentRequests.forEach { userUid ->
            val doc = db.collection("users").document(userUid).get().await()
            doc.toObject(Usuario::class.java)?.let {
                requests.add(it)
            }
        }

        return requests
    }

    // Elimina la amistad en los dos documentos para no dejar estados inconsistentes.
    suspend fun removeFriend(friendUid: String) {
        val currentUid = auth.currentUser?.uid ?: return

        db.collection("users").document(currentUid)
            .update("friends", FieldValue.arrayRemove(friendUid))
            .await()

        db.collection("users").document(friendUid)
            .update("friends", FieldValue.arrayRemove(currentUid))
            .await()
    }

    // Recupera el perfil de cualquier usuario a partir de su uid.
    suspend fun getUserById(uid: String): Usuario? {
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(Usuario::class.java)
    }

    // Comprueba si el username ya está cogido antes de permitir el cambio.
    suspend fun isUsernameAvailable(username: String): Boolean {
        val clean = username.trim().lowercase()

        val snapshot = db.collection("users")
            .whereEqualTo("usernameLowercase", clean)
            .get()
            .await()

        return snapshot.isEmpty
    }

}
