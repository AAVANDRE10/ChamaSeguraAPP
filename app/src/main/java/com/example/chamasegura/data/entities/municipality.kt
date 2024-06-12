package com.example.chamasegura.data.entities

data class Municipality(
    val id: Int,
    val taxNumber: String,
    val address: String,
    val responsible: String,
    val state: State,
    val userId: Int,
    val createdAt: String,
    val updatedAt: String
)

enum class State {
    APPROVED,
    PENDING,
    PENDINGCM,
    DENIED,
    DELETED
}