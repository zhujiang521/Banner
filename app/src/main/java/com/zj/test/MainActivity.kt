package com.zj.test

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.zj.test.ui.theme.BannerTheme
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var mSensorManager: SensorManager
    private lateinit var mMagneticSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        setAndroidNativeLightStatusBar()
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // 陀螺仪传感器
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_GAME)

        setContent {
            BannerTheme {
                val xState by viewModel.xState.observeAsState(0f)
                val yState by viewModel.yState.observeAsState(0f)
                Surface(color = MaterialTheme.colors.background) {
                    ThreeDImage(xState, yState)
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                // x,y,z分别存储坐标轴x,y,z上的加速度
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                refreshState(x, y)
                Log.d(TAG, "TYPE_ACCELEROMETER x:$x y:$y z:$z")
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                // 三个坐标轴方向上的电磁强度，单位是微特拉斯(micro-Tesla)，用uT表示，也可以是高斯(Gauss),1Tesla=10000Gauss
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                refreshState(x, y)
                Log.d(TAG, "TYPE_MAGNETIC_FIELD x:$x y:$y z:$z")
            }
        }
    }

    private fun refreshState(x: Float, y: Float) {
        viewModel.onxStateChanged(x)
        viewModel.onyStateChanged(y)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged: accuracy:$accuracy")
    }

    override fun onDestroy() {
        super.onDestroy()
        mSensorManager.unregisterListener(this)
    }

}