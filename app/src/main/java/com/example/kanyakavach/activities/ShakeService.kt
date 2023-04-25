package com.example.kanyakavach.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import com.example.kanyakavach.R
import com.wafflecopter.multicontactpicker.ContactResult
import java.text.SimpleDateFormat
import java.util.Locale

class ShakeService : Service() {

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationId = 1
    private val channelId = "ShakeDetector"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //val results = intent.getParcelableArrayListExtra<ContactResult>("Extra_Name")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector()
        shakeDetector.setOnShakeListener {
            showNotification()
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Shake Detector", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Shake Detector")
            .setContentText("Listening for shakes")
            .setSmallIcon(R.drawable.ic_launcher_foreground)

        startForeground(notificationId, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val isToggleChecked = intent?.getBooleanExtra("isToggleChecked", false) ?: false
        startShakeDetection()
        if (!isToggleChecked) {
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopShakeDetection()
    }

    private fun showNotification() {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val currentTime = System.currentTimeMillis()
            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Shake Detected")
                .setContentText("Your device was shaken at ${SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(currentTime)}")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
            notificationManager.notify(notificationId, notification)


            val results: ArrayList<ContactResult> = ArrayList()
            //results[0].phoneNumbers.add("9569120913")
            //results.add( 0,9569120913)
            //results.add("9569120913")
//            try {
//                if (!results.isEmpty()) {
//                    for (result in results) {
//                        for (phoneNumber in result.phoneNumbers) {
//                            val smsManager: SmsManager = SmsManager.getDefault()
//                            smsManager.sendTextMessage(
//                                phoneNumber.number,
//                                null,
//                                "Alert Message",
//                                null,
//                                null
//                            )
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                throw RuntimeException(e)
//            }

            //basic
            val phoneNumber = "9569120913" // Replace with your desired phone number
            val city ="New Delhi"
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                phoneNumber,
                null,
                "I need help. My location is at +$city",
                null,
                null
            )

            //sos
            //////
        }
    }

    private fun startShakeDetection() {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    private fun stopShakeDetection() {
        sensorManager.unregisterListener(shakeDetector)
    }
}
