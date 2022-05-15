package com.example.todo.activities

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.todo.*
import com.example.todo.repositories.TaskRepository
import com.example.todo.database.ToDoDatabase
import com.example.todo.database.entities.TaskEntity
import com.example.todo.utils.SettingsHandler
import com.example.todo.utils.TimeHandler
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class AddTaskActivity : ActivityBase(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val manageAttachmentsRequestCode = 1
    private val timeHandler = TimeHandler()
    private lateinit var settings: SettingsHandler
    private lateinit var taskRepository: TaskRepository
    private var attachmentsList = "[]"
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        settings = (application as ToDoApplication).settings
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

        attachmentsLayout.setOnClickListener {
            if (checkWriteStoragePermission()) {
                val intent = Intent(this, AttachmentsActivity::class.java)
                intent.putExtra(attachmentsListIntentKey, attachmentsList)
                startActivityForResult(intent, manageAttachmentsRequestCode)
            } else {
                requestWriteStoragePermission()
            }
        }

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
                    attachmentsList=attachmentsList,
                    sendNotification = sendNotification
                )
                GlobalScope.launch {
                    val rowId = taskRepository.addTask(entity)
                    if (entity.sendNotification) {
                        val rowIdInt = rowId.toInt()
                        (application as ToDoApplication).scheduleNotification(
                            rowIdInt,
                            entity.title,
                            entity.dueDate
                        )
                    }
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Incorrect task details!", Toast.LENGTH_LONG).show()
            }
        }
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
        addCategoryInputText.setAdapter(arrayAdapter)

        val savedAttachmentsList = savedInstanceState.getString(attachmentsListIntentKey, "[]")
        if (savedAttachmentsList != null) {
            attachmentsList = savedAttachmentsList
        }
    }
}
