package com.example.chamasegura.data.entities

data class User(
    val id: Int,
    val name: String,
    val nif: Int? = null,
    val email: String,
    val password: String,
    val photo: String?,
    val type: UserType,
    val createdAt: String,
    val updatedAt: String
)

enum class UserType {
    REGULAR,
    ICNF,
    CM
}

enum class StateUser {
    ENABLED,
    DISABLED
}