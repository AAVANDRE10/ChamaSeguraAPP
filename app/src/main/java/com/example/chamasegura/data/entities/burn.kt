package com.example.chamasegura.data.entities

data class Burn(
    val id: Int,
    val date: String,
    val reason: String,
    val latitude: Float,
    val longitude: Float,
    val otherData: String?,
    val createdAt: String,
    val updatedAt: String,
    val userId: Int,
    val type: BurnType,
    val distrito: String,
    val concelho: String,
    val freguesia: String
)

enum class BurnType {
    REGCLEAN,
    PARTICULAR
}