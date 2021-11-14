package com.example.simpletodo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.nio.charset.Charset
import java.util.Date

class EditTask : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_task)

        val task = intent.getStringExtra("Task")

        val inputField = findViewById<EditText>(R.id.editTaskField)
        inputField.setText(task)

        // Detect when user clicks on Back button
        findViewById<Button>(R.id.backbutton).setOnClickListener {
            val data = Intent()
            setResult(RESULT_CANCELED, data)
            finish()
        }

        // Detect when user clicks on Add button
        findViewById<Button>(R.id.addbutton).setOnClickListener {
            val data = Intent()

            // get text that user inputs into editTaskField
            val userInput = inputField.text.toString()

            data.putExtra("Task", userInput)
            data.putExtra("Position", intent.getIntExtra("Position", 0))
            setResult(RESULT_OK, data)

            Log.i("EditTask", "Item $userInput ${intent.getIntExtra("Position", 0)}")
            finish()
        }
    }
}