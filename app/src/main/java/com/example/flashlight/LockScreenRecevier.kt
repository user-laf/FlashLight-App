package com.example.flashlight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.camera2.CameraManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class LockScreenReceiver : BroadcastReceiver() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cameraId: String
    private lateinit var cameraManager: CameraManager

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            // 通过找到的后置摄像头ID，打开闪光灯
            cameraManager.setTorchMode(cameraId, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val homeActivity = HomeActivity.getInstance()
        homeActivity?.timer?.cancel()
        homeActivity?.binding?.mainBtn?.isSelected = false
        homeActivity?.binding?.head?.isSelected = false
        homeActivity?.binding?.seekbar?.isSelected = false
        homeActivity?.binding?.offOn?.text = "OFF"
        homeActivity?.currentLine?.setImageResource(R.drawable.headline_off_selected)
        homeActivity?.currentNum?.isSelected = false
        homeActivity?.isSwitchOff = true
    }
}