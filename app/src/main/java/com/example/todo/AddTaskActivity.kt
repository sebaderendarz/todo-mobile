package com.example.todo


import android.content.Intent
import android.os.Bundle
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddTaskActivity : ActivityBase() {
    private lateinit var database: ToDoDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        database = Room.databaseBuilder(
            applicationContext, ToDoDatabase::class.java, "To_Do"
        ).build()
        save_button.setOnClickListener {
            if (add_title.text.toString().trim { it <= ' ' }.isNotEmpty()
                && add_priority.text.toString().trim { it <= ' ' }.isNotEmpty()
            ) {
                var title = add_title.getText().toString()
                var priority = add_priority.getText().toString()
                DataObject.setData(title, priority)
                GlobalScope.launch {
                    database.dao().insertTask(Entity(0, title, priority))

                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}

