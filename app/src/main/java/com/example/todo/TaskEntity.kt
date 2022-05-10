package com.example.todo

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String = "",
    var description: String = "",
    var category: String = "Home",
    var createdAt: Long,
    var dueDate: Long,
    var timeZone: String = "CET",
    var sendNotification: Boolean = true,
    var isActive: Boolean = true,
    var hasAttachments: Boolean = true
)
