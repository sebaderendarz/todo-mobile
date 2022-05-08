package com.example.todo

import androidx.room.*

@Dao
interface DAO {
    @Insert
    suspend fun insertTask(entity: Entity)

    @Update
    suspend fun updateTask(entity: Entity)

    @Delete
    suspend fun deleteTask(entity: Entity)

    @Query("Select * from to_do")
    suspend fun getTasks():List<CardInfo>

}