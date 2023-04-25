package com.example.kanyakavach.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import com.example.kanyakavach.R
import com.wafflecopter.multicontactpicker.ContactResult

class MainActivity : AppCompatActivity() {

    private lateinit var toggleButton: ToggleButton
    private lateinit var startServiceButton: Button
    private lateinit var stopServiceButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve the results ArrayList from the intent
        val results = intent.getParcelableArrayListExtra<ContactResult>("Extra_Name")


        toggleButton = findViewById(R.id.togglebutton)
        startServiceButton = findViewById(R.id.start_service_button)
        stopServiceButton = findViewById(R.id.stop_service_button)

        startServiceButton.setOnClickListener {
            val intent = Intent(this, ShakeService::class.java)
            intent.putExtra("isToggleChecked", toggleButton.isChecked)
            intent.putParcelableArrayListExtra(contacts.Extra_Name, results)
            startService(intent)
            Toast.makeText(this,"service running now",Toast.LENGTH_LONG).show()
        }

        stopServiceButton.setOnClickListener {
            stopService(Intent(this, ShakeService::class.java))
            Toast.makeText(this,"service stopping now",Toast.LENGTH_LONG).show()

        }

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startServiceButton.isEnabled = true
                stopServiceButton.isEnabled = false
            } else {
                startServiceButton.isEnabled = false
                stopServiceButton.isEnabled = true
            }
        }


    }
}