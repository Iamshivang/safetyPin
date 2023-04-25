package com.example.kanyakavach.activities

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ShakeDetector : SensorEventListener {

    private var listener: (() -> Unit)? = null
    private var accelerationThreshold: Float = 4.0f
    private var lastUpdateTime: Long = 0
    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f

    fun setOnShakeListener(listener: () -> Unit) {
        this.listener = listener
    }

    fun setThreshold(accelerationThreshold: Float) {
        this.accelerationThreshold = accelerationThreshold
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdateTime < 150) {
                return
            }

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val deltaX = x - lastX
            val deltaY = y - lastY
            val deltaZ = z - lastZ

            lastX = x
            lastY = y
            lastZ = z
            lastUpdateTime = currentTime

            val acceleration = Math.sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()).toFloat() / SensorManager.GRAVITY_EARTH
            if (acceleration >= accelerationThreshold) {
                listener?.invoke()
            }
        }
    }
}

