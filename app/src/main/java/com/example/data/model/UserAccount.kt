package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Local account used by the in-app login screen. Roles: "ADMIN" or "USER". */
@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val salt: String,
    val role: String,
    val createdAt: Long = System.currentTimeMillis()
)
