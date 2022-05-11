package com.example.todo

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): TaskEntity

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<TaskEntity>>

    @Insert
    suspend fun addTask(entity: TaskEntity)

    @Update
    suspend fun updateTask(entity: TaskEntity)

    @Delete
    suspend fun deleteTask(entity: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
}