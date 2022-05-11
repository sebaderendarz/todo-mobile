package com.example.todo.repositories

import androidx.lifecycle.LiveData
import com.example.todo.database.daos.TaskDao
import com.example.todo.database.entities.TaskEntity

class TaskRepository(private val taskDao: TaskDao) {

    val getAllTasks: LiveData<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTaskById(taskId: Int): TaskEntity {
        return taskDao.getTaskById(taskId)
    }

    suspend fun addTask(task: TaskEntity){
        taskDao.addTask(task)
    }

    suspend fun updateTask(task: TaskEntity){
        taskDao.updateTask(task)
    }

    suspend fun deleteTaskById(taskId: Int){
        taskDao.deleteTaskById(taskId)
    }
}