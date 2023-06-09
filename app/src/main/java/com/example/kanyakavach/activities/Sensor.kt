package com.example.kanyakavach.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.kanyakavach.R
import com.example.kanyakavach.activities.contacts.Companion.Extra_Name

import com.wafflecopter.multicontactpicker.ContactResult
import com.wafflecopter.multicontactpicker.MultiContactPicker
import kotlinx.android.synthetic.main.activity_sensor.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Sensor : AppCompatActivity(), SensorEventListener {
    var count = 0
    private val CONTACT_PICKER_REQUEST = 202

    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private val SHAKE_THRESHOLD_GRAVITY = 5.7f
    private var mLastShakeTime: Long = 0
    private var isShakeDetectionEnabled = false
    var results: ArrayList<ContactResult> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)
//        results.add("7668257571")
//        results.add("9569120913")
        if(intent.hasExtra(Extra_Name))
        {
            results= intent.getSerializableExtra(contacts.Extra_Name) as ArrayList<ContactResult>
        }

        Log.e("contacts", results.toString())

        Log.e("Numbers", results.toString())

        val detectionToggle = findViewById<ToggleButton>(R.id.detection_toggle)
        detectionToggle.setOnCheckedChangeListener { _, isChecked ->
            isShakeDetectionEnabled = isChecked
        }
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    }

    override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager!!.unregisterListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent) {
        if (!isShakeDetectionEnabled) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - mLastShakeTime > 2000) {
                mLastShakeTime = currentTime
                Toast.makeText(this, "Shake detected!", Toast.LENGTH_SHORT).show()

                val currentTime = System.currentTimeMillis()
                val channelId = "shake_detection_channel"
                val channelName = "Shake Detection"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val notificationChannel = NotificationChannel(channelId, channelName, importance)
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(notificationChannel)

                val notificationBuilder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Shake Detected!")
                    .setContentText(
                        "Your device was shaken at ${
                            SimpleDateFormat(
                                "hh:mm:ss a",
                                Locale.getDefault()
                            ).format(currentTime)
                        }"
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                notificationManager.notify(0, notificationBuilder.build())

                try {
                    if (!results.isEmpty()) {
                        for (j in results.indices) {
                            for (i in 0 until 2) {
                                val smsManager: SmsManager = SmsManager.getDefault()
                                smsManager.sendTextMessage(
                                    results[j].phoneNumbers[0].number,
                                    null,
                                    "Alert Message",
                                    null,
                                    null
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }

                //////SOS
                ////////
            }
        }

    }

    private fun setUpActionBar()
    {
        setSupportActionBar(toolbar_sensors_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        toolbar_sensors_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        TODO("Not yet implemented")
    }

}