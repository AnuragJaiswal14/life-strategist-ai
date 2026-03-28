package com.aistrategist.app.domain.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signInWithGoogleToken(idToken: String): Result<FirebaseUser?>
    fun getCurrentUser(): FirebaseUser?
    fun signOut()
}
