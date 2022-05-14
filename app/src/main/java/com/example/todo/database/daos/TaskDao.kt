package com.example.todo.database.daos

import androidx.room.*
import com.example.todo.database.entities.TaskEntity

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): TaskEntity

    @Query("SELECT * FROM tasks WHERE title LIKE '%'|| :title || '%'")
    fun getTasks(title: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE title LIKE '%'|| :title || '%' ORDER BY dueDate ASC")
    fun getTasksOrderedByDueDate(title: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE title LIKE '%'|| :title || '%' AND isActive = 1")
    fun getActiveTasks(title: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE title LIKE '%'|| :title || '%' AND isActive = 1 ORDER BY dueDate ASC")
    fun getActiveTasksOrderedByDueDate(title: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE title LIKE '%'|| :title || '%' AND category = :category")
    fun getTasksByCategory(title: String, category: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE title LIKE '%'|| :title || '%' AND category = :category ORDER BY dueDate ASC")
    fun getTasksByCategoryOrderedByDueDate(title: String, category: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE title LIKE '%'|| :title || '%' AND isActive = 1 AND category = :category")
    fun getActiveTasksByCategory(title: String, category: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE title LIKE '%'|| :title || '%' AND isActive = 1 AND category = :category ORDER BY dueDate ASC")
    fun getActiveTasksByCategoryOrderedByDueDate(title: String, category: String): List<TaskEntity>

    @Insert
    suspend fun addTask(entity: TaskEntity): Long

    @Update
    suspend fun updateTask(entity: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
}