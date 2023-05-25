package com.example.flashlight

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager

class MyApplication : Application() {

    private lateinit var  sharedPreferences : SharedPreferences
    private lateinit var cameraId: String
    private lateinit var cameraManager:CameraManager

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        for (id in cameraManager.cameraIdList) {
            //获取摄像头的特性对象
            val characteristics = cameraManager.getCameraCharacteristics(id)
            //获取摄像头的朝向，前置还是后置
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            //如果是后置摄像头，将摄像头ID存储到cameraID，退出遍历
            if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                cameraId = id
                break
            }
        }
        registerActivityLifecycleCallbacks(AppLifecycleCallbacks())
    }

    private inner class AppLifecycleCallbacks : ActivityLifecycleCallbacks {


        private var count = 0

        override fun onActivityCreated(p0: Activity, p1: Bundle?) {

        }

        override fun onActivityStarted(p0: Activity) {
            count++
        }

        override fun onActivityResumed(p0: Activity) {
        }

        override fun onActivityPaused(p0: Activity) {
        }

        override fun onActivityStopped(p0: Activity) {
            count--
            if (count == 0) {
                val switch2 = sharedPreferences.getBoolean("switch2", false)
                if (!switch2) {
                    try {
                        // 通过找到的后置摄像头ID，打开闪光灯
                        cameraManager.setTorchMode(cameraId, false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val homeActivity = HomeActivity.getInstance()
                    homeActivity?.timer?.cancel()
                    homeActivity?.binding?.mainBtn?.isSelected = false   //主按钮亮
                    homeActivity?.binding?.head?.isSelected = false      //顶部光亮
                    homeActivity?.binding?.seekbar?.isSelected = false   //滑块亮
                    homeActivity?.binding?.offOn?.text = "OFF"           //开关下方字体更换
                    homeActivity?.currentLine?.setImageResource(R.drawable.headline_off_selected)
                    homeActivity?.currentNum?.isSelected = false
                    homeActivity?.isSwitchOff = true
                }
            }
        }

        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        }

        override fun onActivityDestroyed(p0: Activity) {
        }



    }



}

