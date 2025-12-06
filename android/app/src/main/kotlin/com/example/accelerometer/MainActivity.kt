package com.example.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel

class MainActivity : FlutterActivity() {

    var lastEmittedMs: Long = 0
    var totalFetchThisSecond = 0
    var eventSink: EventChannel.EventSink? = null
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private var isListenerRegistered = false

    private var asdasda = System.currentTimeMillis()

    companion object {
        private const val CHANNEL_BASE = "accelerometer_stream"
        private const val INIT_SENSOR_METHOD = "init_sensor"
    }

    val sensorEventListener = object : SensorEventListener {

        override fun onSensorChanged(sensorEvent: SensorEvent?) {
            val event = sensorEvent ?: return
            val values = event.values ?: return

            val x = values[0].toDouble()
            val y = values[1].toDouble()
            val z = values[2].toDouble()
            val data = mapOf(
                "x" to x,
                "y" to y,
                "z" to z,
            )


            val currentTimeMs = System.currentTimeMillis()
            val emittedMsDiff = currentTimeMs - lastEmittedMs

            if (emittedMsDiff >= 250) {
                eventSink?.success(data)
                totalFetchThisSecond += 1
                lastEmittedMs = currentTimeMs
            }

            if ((System.currentTimeMillis() - asdasda).toInt() >= 1000) {
                asdasda = System.currentTimeMillis()
            }


        }

        override fun onAccuracyChanged(sensorEvent: Sensor?, p1: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    }

    private fun registerSensorListener() {
        if (!isListenerRegistered && sensor != null) {
            sensorManager.registerListener(
                sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL
            )
            isListenerRegistered = true
            Log.d(this.localClassName, "Sensor listener registered")
        }
    }

    private fun unregisterSensorListener() {
        if (isListenerRegistered) {
            sensorManager.unregisterListener(sensorEventListener)
            isListenerRegistered = false
            Log.d(this.localClassName, "Sensor listener unregistered")
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-register if stream is active
        if (eventSink != null) {
            registerSensorListener()
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterSensorListener()
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val eventChannel = EventChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_BASE)
        eventChannel.setStreamHandler(object : EventChannel.StreamHandler {

            override fun onCancel(arguments: Any?) {
                Log.d(this@MainActivity.localClassName, "Stream cancelled")
                eventSink = null
                unregisterSensorListener()
            }

            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                Log.d(this@MainActivity.localClassName, "Stream listener started")
                eventSink = events
                registerSensorListener()
            }
        })
    }
}
