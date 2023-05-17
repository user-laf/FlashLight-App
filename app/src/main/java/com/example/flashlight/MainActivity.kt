package com.example.flashlight


import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.example.flashlight.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var toggleButton: ToggleButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Settings.setOnClickListener {
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.start.setOnClickListener {
            val intent = Intent(this,StartActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener {
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }

        /************************************************************/

        // 获取相机服务并实例化CameraManager类
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // 获取可用的相机列表
            val cameraIds = cameraManager.cameraIdList
            // 如果没有可用相机，则退出应用
            if (cameraIds.isEmpty()) {
                Toast.makeText(this, "No cameras available", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // 使用第一个相机ID
                cameraId = cameraIds[0]
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        // 设置ToggleButton的OnCheckedChangeListener侦听器
        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            // 根据ToggleButton状态控制开关闪光灯
            try {
                cameraManager.setTorchMode(cameraId, isChecked)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

    }
}