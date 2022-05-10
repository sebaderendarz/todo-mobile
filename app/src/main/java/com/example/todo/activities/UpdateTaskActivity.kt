package com.example.todo.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.room.Room
import com.example.todo.DataObject
import com.example.todo.Entity
import com.example.todo.R
import com.example.todo.ToDoDatabase
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.activity_update_task.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class UpdateTaskActivity : ActivityBase(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var database: ToDoDatabase
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_task)
        database = Room.databaseBuilder(
            applicationContext, ToDoDatabase::class.java, "To_Do"
        ).build()

        val taskCategories = resources.getStringArray(R.array.TaskCategories)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, taskCategories)
        updateCategoryInputText.setAdapter(arrayAdapter)

        getCurrentCalendarDateTime()
        updateTaskTimeView()

        updateTaskTimeInput.setOnClickListener {
            getCurrentCalendarDateTime()
            DatePickerDialog(this, this, year, month, day).show()
        }

        updateNotifyLayoutInput.setOnClickListener {
            if (updateNotifyLayoutInput.text.toString() == "Notify") {
                updateNotifyLayoutInput.setText("Muted")
            } else {
                updateNotifyLayoutInput.setText("Notify")
            }
        }

        updateStatusLayoutInput.setOnClickListener {
            if (updateStatusLayoutInput.text.toString() == "Pending") {
                updateStatusLayoutInput.setText("Done")
            } else {
                updateStatusLayoutInput.setText("Pending")
            }
        }

        val pos = intent.getIntExtra("id", -1)
        if (pos != -1) {
            val title = DataObject.getData(pos).title
            val priority = DataObject.getData(pos).priority
            updateTitleInputText.setText(title)
            updateDescriptionInputText.setText(priority)

            deleteButton.setOnClickListener {
                DataObject.deleteData(pos)
                GlobalScope.launch {
                    database.dao().deleteTask(
                        Entity(
                            pos + 1,
                            updateTitleInputText.text.toString(),
                            updateDescriptionInputText.text.toString()
                        )
                    )
                }
                myIntent()
            }

            updateButton.setOnClickListener {
                DataObject.updateData(
                    pos,
                    updateTitleInputText.text.toString(),
                    updateDescriptionInputText.text.toString()
                )
                GlobalScope.launch {
                    database.dao().updateTask(
                        Entity(
                            pos + 1, updateTitleInputText.text.toString(),
                            updateDescriptionInputText.text.toString()
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

    private fun getCurrentCalendarDateTime() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("CET"))
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
    }

    private fun updateTaskTimeView() {
        val monthWithLeadingZero = if (month < 10) {
            "0$month"
        } else {
            month.toString()
        }
        val dayWithLeadingZero = if (day < 10) {
            "0$day"
        } else {
            day.toString()
        }
        val hourWithLeadingZero = if (hour < 10) {
            "0$hour"
        } else {
            hour.toString()
        }
        val minuteWithLeadingZero = if (minute < 10) {
            "0$minute"
        } else {
            minute.toString()
        }
        updateTaskTimeInput.setText("$year-$monthWithLeadingZero-$dayWithLeadingZero $hourWithLeadingZero:$minuteWithLeadingZero")
    }

    override fun onDateSet(view: DatePicker?, yearVal: Int, monthVal: Int, dayOfMonthVal: Int) {
        year = yearVal
        month = monthVal
        day = dayOfMonthVal
        updateTaskTimeView()
        TimePickerDialog(this, this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDayVal: Int, minuteVal: Int) {
        hour = hourOfDayVal
        minute = minuteVal
        updateTaskTimeView()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val taskCategories = resources.getStringArray(R.array.TaskCategories)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, taskCategories)
        updateCategoryInputText.setAdapter(arrayAdapter)
    }
}
