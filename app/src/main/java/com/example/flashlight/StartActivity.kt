package com.example.flashlight

import android.content.Intent
import android.os.Bundle
import android.view.animation.*
import androidx.appcompat.app.AppCompatActivity
import com.example.flashlight.databinding.ActivityStartBinding
import java.util.*


class StartActivity : AppCompatActivity() {

    private lateinit var timer: Timer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //获取到当前设备屏幕上一个像素对应的 dp 值
        val density = resources.displayMetrics.density

        //标题透明度变化
        val alphaAnimation = AlphaAnimation(0.3f, 1.0f)
        alphaAnimation.duration = 1000

        //标题文字的移动效果
        val translateAnimation1 = TranslateAnimation(
            binding.title1.x, //x轴起始位置
            binding.title1.x, //x轴最终位置
            binding.title1.y, //y轴起始位置
            binding.title1.y + 20.0f * density //y轴最终位置
        )
        translateAnimation1.duration = 1000 //移动持续1秒

        //副标题文字移动效果
        val translateAnimation2 = TranslateAnimation(
            binding.title2.x, //x轴起始位置
            binding.title2.x, //x轴最终位置
            binding.title2.y, //y轴起始位置
            binding.title2.y - 20.0f * density //y轴最终位置
        )
        translateAnimation2.duration = 1000 //移动持续1秒

        //fillAfter = true 是 Animation 类的一个属性，意思是在动画执行结束后，View 会停留在动画结束的状态
        AnimationSet(true).apply {
            fillAfter = true
            addAnimation(alphaAnimation)
            addAnimation(translateAnimation1)
            binding.title1.startAnimation(this)
        }

        AnimationSet(true).apply {
            fillAfter = true
            addAnimation(alphaAnimation)
            addAnimation(translateAnimation2)
            binding.title2.startAnimation(this)
        }

        //光束透明度
        AlphaAnimation(0.1f, 1.0f).apply {
            duration = 300
            binding.startFront.startAnimation(this)
        }

        /**--------------------------------------**/

        //图片左移动画
        val translateAnimation3 = TranslateAnimation(
            binding.startBack.x,
            binding.startBack.x-80.0f*density,
            binding.startBack.y,
            binding.startBack.y
        )
        translateAnimation3.duration=500

        //图片右移动画
        val translateAnimation4 = TranslateAnimation(
            binding.startBack.x,
            binding.startBack.x+80.0f*density,
            binding.startBack.y,
            binding.startBack.y
        )
        translateAnimation4.duration=500
        translateAnimation4.startOffset = 500

        AnimationSet(true).apply {
            fillAfter = true
            addAnimation(translateAnimation3)
            addAnimation(translateAnimation4)
            binding.startBack.startAnimation(this)
            binding.startFront.startAnimation(this)
        }

        // 创建定时器
        timer = Timer()
        // 延迟启动 HomeActivity
        timer.schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(this@StartActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

}