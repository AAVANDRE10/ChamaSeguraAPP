package com.example.chamasegura.utils

import com.auth0.android.jwt.JWT

object JwtUtils {

    fun getUserIdFromToken(token: String): Int? {
        return try {
            val jwt = JWT(token)
            jwt.getClaim("id").asInt()
        } catch (e: Exception) {
            null
        }
    }
}
