package com.example.todo.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.todo.*
import com.example.todo.repositories.TaskRepository
import com.example.todo.database.ToDoDatabase
import com.example.todo.database.entities.TaskEntity
import com.example.todo.notifications.*
import com.example.todo.notifications.Notification
import com.example.todo.utils.SettingsHandler
import com.example.todo.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class AddTaskActivity : ActivityBase(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val timeHandler = TimeHandler()
    private lateinit var settings: SettingsHandler
    private lateinit var taskRepository: TaskRepository
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        settings = SettingsHandler(applicationContext)
        taskRepository = TaskRepository(ToDoDatabase.getDatabase(applicationContext).taskDao())

        val taskCategories = resources.getStringArray(R.array.TaskCategories)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, taskCategories)
        addCategoryInputText.setAdapter(arrayAdapter)

        getCurrentCalendarDateTime()
        updateTaskTimeView()
        addTaskTimeInput.setOnClickListener {
            getCurrentCalendarDateTime()
            DatePickerDialog(this, this, year, month, day).show()
        }
        notifyLayoutInput.setOnClickListener {
            if (notifyLayoutInput.text.toString() == "Notify") {
                notifyLayoutInput.setText("Muted")
            } else {
                notifyLayoutInput.setText("Notify")
            }
        }

        createNotificationChannel()

        saveButton.setOnClickListener {
            if (addTitleInputText.text.toString().trim { it <= ' ' }.isNotEmpty()
                && addDescriptionInputText.text.toString().trim { it <= ' ' }.isNotEmpty()
            ) {
                val sendNotification = notifyLayoutInput.text.toString() != "Muted"
                val entity = TaskEntity(
                    0,
                    addTitleInputText.text.toString(),
                    addDescriptionInputText.text.toString(),
                    addCategoryInputText.text.toString(),
                    timeHandler.generateEpochForCurrentTime(),
                    timeHandler.generateEpochFromTimeValues(year, month + 1, day, hour, minute),
                    sendNotification = sendNotification
                )
                GlobalScope.launch {
                    val rowId = taskRepository.addTask(entity)
                    if (entity.sendNotification) {
                        val rowIdInt = rowId.toInt()
                        scheduleNotification(rowIdInt, entity.title, entity.dueDate)
                    }
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Incorrect task details!", Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Due Date Channel"
        val description = "Notify That the Task's Deadline Looming"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotification(notificationId: Int, title: String, time: Long) {
        val intent = Intent(applicationContext, Notification::class.java)
        val minutesBefore = settings.getNotificationTime()
        val message = "Deadline is looming! $minutesBefore minutes remaining"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationTime = (time - minutesBefore * 60) * 1000
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notificationTime,
            pendingIntent
        )
        println("notification scheduled for ID: $notificationId and title: $title")
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
        val timeString =
            timeHandler.generateTimeStringFromTimeValues(year, month + 1, day, hour, minute)
                .dropLast(3)
        addTaskTimeInput.setText(timeString)
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
        addCategoryInputText.setAdapter(arrayAdapter)
    }
}
