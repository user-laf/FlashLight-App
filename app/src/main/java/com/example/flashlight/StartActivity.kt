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
        setContentView(R.layout.activity_start)

        val binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //获取到当前设备屏幕上一个像素对应的 dp 值
        val density = resources.displayMetrics.density

        //标题文字的移动效果
        val translateAnimation1 = TranslateAnimation(
            binding.title1.x, //x轴起始位置
            binding.title1.x, //x轴最终位置
            binding.title1.y, //y轴起始位置
            binding.title1.y + 20.0f * density //y轴最终位置
        )
        val alphaAnimation1 = AlphaAnimation(0.3f, 1.0f) //透明度由30%变化为100%
        translateAnimation1.duration = 1000 //移动持续1秒
        alphaAnimation1.duration = 1000     //透明度变化持续1秒

        //副标题文字移动效果
        val translateAnimation2 = TranslateAnimation(
            binding.title2.x, //x轴起始位置
            binding.title2.x, //x轴最终位置
            binding.title2.y, //y轴起始位置
            binding.title2.y - 20.0f * density //y轴最终位置
        )
        val alphaAnimation2 = AlphaAnimation(0.3f, 1.0f)
        translateAnimation2.duration = 1000 //移动持续1秒
        alphaAnimation2.duration = 1000     //透明度变化持续1秒

          //光束透明度
        val alphaAnimation3 = AlphaAnimation(0.1f, 1.0f)
        alphaAnimation3.duration = 300
        binding.startFront.startAnimation(alphaAnimation3)

        val animationSet1 = AnimationSet(true)
        val animationSet2 = AnimationSet(true)
        animationSet1.fillAfter = true
        animationSet2.fillAfter = true
        animationSet1.addAnimation(translateAnimation1)
        animationSet1.addAnimation(alphaAnimation1)
        animationSet2.addAnimation(translateAnimation2)
        animationSet2.addAnimation(alphaAnimation2)
        binding.title1.startAnimation(animationSet1)
        binding.title2.startAnimation(animationSet2)
        /**--------------------------------------**/

        val translateAnimation3 = TranslateAnimation(
            binding.startBack.x,
            binding.startBack.x-80.0f*density,
            binding.startBack.y,
            binding.startBack.y

        )
        translateAnimation3.duration=500
        val translateAnimation4 = TranslateAnimation(
            binding.startBack.x,
            binding.startBack.x+80.0f*density,
            binding.startBack.y,
            binding.startBack.y

        )
        translateAnimation4.duration=500
        translateAnimation4.startOffset = 500
        val AnimationSet3 = AnimationSet(true)
        AnimationSet3.fillAfter = true
        AnimationSet3.addAnimation(translateAnimation3)
        AnimationSet3.addAnimation(translateAnimation4)
        binding.startBack.startAnimation(AnimationSet3)
        binding.startFront.startAnimation(AnimationSet3)

        // 创建定时器
        timer = Timer()

        // 延迟 2 秒启动 MainActivity
        timer.schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(this@StartActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1300)
    }

    override fun onDestroy() {
        super.onDestroy()

        timer.cancel()
    }


}