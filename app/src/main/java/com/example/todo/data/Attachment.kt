package com.example.todo.data

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val path: String = "",
    val name: String = "",
)
