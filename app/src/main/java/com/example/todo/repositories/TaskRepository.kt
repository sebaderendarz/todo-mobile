package com.example.todo.repositories

import com.example.todo.database.daos.TaskDao
import com.example.todo.database.entities.TaskEntity

class TaskRepository(private val taskDao: TaskDao) {

    fun getTaskById(taskId: Int): TaskEntity {
        return taskDao.getTaskById(taskId)
    }

    fun getTasks(title: String): List<TaskEntity> {
        return taskDao.getTasks(title)
    }

    fun getTasksOrderedByDueDate(title: String): List<TaskEntity> {
        return taskDao.getTasksOrderedByDueDate(title)
    }

    fun getActiveTasks(title: String): List<TaskEntity> {
        return taskDao.getActiveTasks(title)
    }

    fun getActiveTasksOrderedByDueDate(title: String): List<TaskEntity>{
        return taskDao.getActiveTasksOrderedByDueDate(title)
    }

    fun getTasksByCategory(title: String, category: String): List<TaskEntity> {
        return taskDao.getTasksByCategory(title, category)
    }

    fun getTasksByCategoryOrderedByDueDate(title: String, category: String): List<TaskEntity> {
        return taskDao.getTasksByCategoryOrderedByDueDate(title, category)
    }

    fun getActiveTasksByCategory(title: String, category: String): List<TaskEntity> {
        return taskDao.getActiveTasksByCategory(title, category)
    }

    fun getActiveTasksByCategoryOrderedByDueDate(title: String, category: String): List<TaskEntity> {
        return taskDao.getActiveTasksByCategoryOrderedByDueDate(title, category)
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