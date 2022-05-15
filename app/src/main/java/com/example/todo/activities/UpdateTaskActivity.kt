package com.example.todo.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.todo.*
import com.example.todo.database.ToDoDatabase
import com.example.todo.database.entities.TaskEntity
import com.example.todo.repositories.TaskRepository
import com.example.todo.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_update_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class UpdateTaskActivity : ActivityBase(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val manageAttachmentsRequestCode = 1
    private val timeHandler = TimeHandler()
    private lateinit var taskRepository: TaskRepository
    private var attachmentsList = "[]"
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_task)

        taskRepository = TaskRepository(ToDoDatabase.getDatabase(applicationContext).taskDao())

        val taskCategories = resources.getStringArray(R.array.TaskCategories)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, taskCategories)
        updateCategoryInputText.setAdapter(arrayAdapter)

        getCurrentCalendarDateTime()

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

        val taskId = intent.getIntExtra("taskId", 0)
        if (taskId > 0) {
            val ref = this
            lifecycleScope.launch(Dispatchers.IO) {
                val taskEntity = taskRepository.getTaskById(taskId)
                ref.runOnUiThread {
                    updateTitleInputText.setText(taskEntity.title)
                    updateDescriptionInputText.setText(taskEntity.description)
                    updateCategoryInputText.setText(taskEntity.category, false)
                    updateTaskTimeInput.setText(timeHandler.generateTimeStringFromEpoch(taskEntity.dueDate))
                    createdAtTaskTimeInput.setText(
                        timeHandler.generateTimeStringFromEpoch(
                            taskEntity.createdAt
                        )
                    )
                    if (attachmentsList == "[]") {
                        attachmentsList = taskEntity.attachmentsList
                    }
                    if (taskEntity.sendNotification) {
                        updateNotifyLayoutInput.setText("Notify")
                    } else {
                        updateNotifyLayoutInput.setText("Muted")
                    }
                    if (taskEntity.isActive) {
                        updateStatusLayoutInput.setText("Pending")
                    } else {
                        updateStatusLayoutInput.setText("Done")
                    }
                }
            }

            updateAttachmentsLayout.setOnClickListener {
                if (checkWriteStoragePermission()) {
                    val intent = Intent(this, AttachmentsActivity::class.java)
                    intent.putExtra(attachmentsListIntentKey, attachmentsList)
                    startActivityForResult(intent, manageAttachmentsRequestCode)
                } else {
                    requestWriteStoragePermission()
                }
            }

            deleteButton.setOnClickListener {
                GlobalScope.launch {
                    taskRepository.deleteTaskById(taskId)
                    (application as ToDoApplication).cancelNotification(taskId)
                }
                mainActivityIntent()
            }

            updateButton.setOnClickListener {
                val sendNotification = updateNotifyLayoutInput.text.toString() != "Muted"
                val isActive = updateStatusLayoutInput.text.toString() != "Done"
                val entity = TaskEntity(
                    taskId,
                    updateTitleInputText.text.toString(),
                    updateDescriptionInputText.text.toString(),
                    updateCategoryInputText.text.toString(),
                    timeHandler.generateEpochFromTimeString(createdAtTaskTimeInput.text.toString() + ":00"),
                    timeHandler.generateEpochFromTimeString(updateTaskTimeInput.text.toString() + ":00"),
                    attachmentsList = attachmentsList,
                    sendNotification = sendNotification,
                    isActive = isActive
                )
                GlobalScope.launch {
                    taskRepository.updateTask(entity)
                }
                if (!isActive || !sendNotification) {
                    (application as ToDoApplication).cancelNotification(taskId)
                } else {
                    (application as ToDoApplication).scheduleNotification(
                        taskId,
                        entity.title,
                        entity.dueDate
                    )
                }
                mainActivityIntent()
            }

        }
    }

    private fun mainActivityIntent() {
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
        val timeString =
            timeHandler.generateTimeStringFromTimeValues(year, month + 1, day, hour, minute)
                .dropLast(3)
        updateTaskTimeInput.setText(timeString)
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

    private fun checkWriteStoragePermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWriteStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(this, "Storage permission is required!", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(),
                111
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (manageAttachmentsRequestCode == requestCode && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                attachmentsList = data.getStringExtra(attachmentsListIntentKey).toString()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(attachmentsListIntentKey, attachmentsList)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val taskCategories = resources.getStringArray(R.array.TaskCategories)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, taskCategories)
        updateCategoryInputText.setAdapter(arrayAdapter)

        val savedAttachmentsList = savedInstanceState.getString(attachmentsListIntentKey, "[]")
        if (savedAttachmentsList != null) {
            attachmentsList = savedAttachmentsList
        }
    }
}
