package com.example.a207395_liuzhaohe_izwan_lab.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounterHelper(
    context: Context,
    private val onStepChanged:(Int)->Unit
): SensorEventListener {

    //接触手机传感器
    private val sensorManager =
        context.getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager

    //调用步数传感器
    private val stepSensor =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_STEP_COUNTER
        )

    //开始监听传感器
    fun start(){
        stepSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stop(){
        sensorManager.unregisterListener(this)
    }

    //获取传感器数据
    override fun onSensorChanged(
        event: SensorEvent?
    ) {
        if(event != null){
            val steps =
                event.values[0].toInt()
            onStepChanged(steps)
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}