package com.example.simpletodo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class EditTask : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_task)

        val task = intent.getStringExtra("Task")
        val taskName = task!!.split("|~|")[0].trim()
        val taskDate = task.split("|~|")[1].trim()
        val taskDesc = task.split("|~|")[2].trim()

        val inputField = findViewById<EditText>(R.id.editTaskField)
        val editDateField = findViewById<DatePicker>(R.id.datePicker)
        val descField = findViewById<EditText>(R.id.editDescField)

        inputField.setText(taskName)
        descField.setText(taskDesc)
        if (taskDate != "") {
            val taskDateParts = taskDate.split("/")
            editDateField.updateDate(
                Integer.parseInt(taskDateParts[2]),
                Integer.parseInt(taskDateParts[0]) - 1,
                Integer.parseInt(taskDateParts[1]))
        }

        // Detect when user clicks on Back button
        findViewById<Button>(R.id.backbutton).setOnClickListener {
            val data = Intent()
            setResult(RESULT_CANCELED, data)
            finish()
        }

        // Detect when user clicks on Add button
        findViewById<Button>(R.id.addbutton).setOnClickListener {
            val data = Intent()

            val calendar: Calendar = Calendar.getInstance()
            calendar.set(editDateField.year, editDateField.month, editDateField.dayOfMonth)

            // get text that user inputs into editTaskField
            val userInput = inputField.text.toString() + " |~| " +
                    SimpleDateFormat("MM/dd/yyyy", Locale.US).format(calendar.time).toString() +
                    " |~| " + descField.text.toString()

            data.putExtra("Task", userInput)
            data.putExtra("Position", intent.getIntExtra("Position", 0))
            setResult(RESULT_OK, data)

            Log.i("EditTask",
                "Item $userInput ${intent.getIntExtra("Position", 0)}")
            finish()
        }
    }
}