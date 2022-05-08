package com.example.todo

import android.content.Intent
import android.os.Bundle
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_update_task.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UpdateTaskActivity : ActivityBase() {
    private lateinit var database: ToDoDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_task)
        database = Room.databaseBuilder(
            applicationContext, ToDoDatabase::class.java, "To_Do"
        ).build()
        val pos = intent.getIntExtra("id", -1)
        if (pos != -1) {
            val title = DataObject.getData(pos).title
            val priority = DataObject.getData(pos).priority
            update_title.setText(title)
            update_priority.setText(priority)

            delete_button.setOnClickListener {
                DataObject.deleteData(pos)
                GlobalScope.launch {
                    database.dao().deleteTask(
                        Entity(
                            pos + 1,
                            update_title.text.toString(),
                            update_priority.text.toString()
                        )
                    )
                }
                myIntent()
            }

            update_button.setOnClickListener {
                DataObject.updateData(
                    pos,
                    update_title.text.toString(),
                    update_priority.text.toString()
                )
                GlobalScope.launch {
                    database.dao().updateTask(
                        Entity(
                            pos + 1, update_title.text.toString(),
                            update_priority.text.toString()
                        )
                    )
                }
                myIntent()
            }

        }
    }

    private fun myIntent() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}