package com.example.todo

import androidx.room.*

@Dao
interface ToDoDao {
    @Insert
    suspend fun insertTask(entity: TaskEntity)

    @Update
    suspend fun updateTask(entity: TaskEntity)

    @Delete
    suspend fun deleteTask(entity: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    @Query("Select * from tasks")
    suspend fun getTasks():List<CardInfo>

}