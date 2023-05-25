package com.example.flashlight


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.SoundPool
import android.os.Bundle
import android.os.Vibrator
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.example.flashlight.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch
import java.util.*

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    private var soundPool1: SoundPool = SoundPool.Builder().build()
    private var soundPool2: SoundPool = SoundPool.Builder().build()
    private var soundId1: Int = 0
    private var soundId2: Int = 0
    private lateinit var vibrator: Vibrator
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String//定义变量用来存储后置摄像头ID
    var isSwitchOff: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private var switch1: Boolean = false
    private var switch3: Boolean = false


    companion object {
        private var instance: HomeActivity? = null

        fun getInstance(): HomeActivity? {
            return instance
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //获取sharedPreferences对象
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        // 保存当前 Activity 实例
        instance = this

        // 注册锁屏广播接收器
        val lockScreenReceiver = LockScreenReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(lockScreenReceiver, filter)


        // 初始化摄像头管理器和查找摄像头ID
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        //遍历摄像头ID
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

        //如果选择了自动开启则打开闪光灯
        if (sharedPreferences.getBoolean("switch1",false)){
            try {
                cameraManager.setTorchMode(cameraId, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            lightOn()
            binding.line0.setImageResource(R.drawable.headline_on)
            binding.num0.isSelected = true
        } else {
            isSwitchOff = true
        }


        //加载音效
        soundId1 = soundPool1.load(this, R.raw.click1, 1)
        soundId2 = soundPool2.load(this, R.raw.light4, 1)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        /************************************************************************/
        //主开关点击事件
        binding.mainBtn.setOnClickListener {
            //震动并根据
            vibrator.vibrate(50)
            //通过开关判断是否开启音效
            if(sharedPreferences.getBoolean("switch3",false)){
                soundPool1.play(soundId1, 0.5f, 0.5f, 1, 0, 1f)
            }
            //通过滑块的位置寻找到线和数字的位置
            val layoutParams = binding.headBtn.layoutParams as ConstraintLayout.LayoutParams
            val line = findViewById<ImageView>(layoutParams.startToStart)
            val num = findViewById<ImageView>(layoutParams.endToEnd)

            if (isSwitchOff) {
                lightOn()
                line.setImageResource(R.drawable.headline_on) //开灯时，被选中的line白亮光
                num.isSelected = true               //开灯时，被选中的num亮

                //开灯时根据当前seekBar位置自动选择频率
                doAction(binding.seekbar.progress)
            } else {
                lightOff()
                line.setImageResource(R.drawable.headline_off_selected)//关灯时，被选中的line为白色
                num.isSelected = false                //关着时，被选中的num不亮

                //关闭闪光灯
                turnOffFlashLight()
                timer?.cancel()
            }
        }

        /**
         * 封装点击事件
         *
         * 点击其他挡位后，需要先关闭当前挡位的光效，然后点亮新点击的挡位
         * 设置一个透明的view当作标记，这个标记始终约束在一组num和line的中间，起始位置在num_0和line_0中间，
         * 在点击其他挡位后先通过标记找到当前点亮的num和line,将他们关闭，
         * 然后通过点击传入的参数将标记约束到新的挡位，并将新的挡位点亮
         * 在操作主按钮开关时，也是通过这个标记找到当前的num和line
         *
         */
        fun selectState(lineId: Int, numId: Int, num_place: Int) {
            val layoutParams = binding.headBtn.layoutParams as ConstraintLayout.LayoutParams
            val line = findViewById<ImageView>(layoutParams.startToStart)
            val num = findViewById<ImageView>(layoutParams.endToEnd)

            // 点击其他line和num后，先将当前的选中状态取消
            line.setImageResource(R.drawable.headline_off_none)
            num.isSelected = false

            // 然后将滑块约束到新点击的line和num下方，设置新的约束关系
            layoutParams.startToStart = lineId
            layoutParams.endToEnd = numId
            binding.headBtn.layoutParams = layoutParams

            vibrator.vibrate(50)
            // 设置新选中的line和num样式
            if (isSwitchOff) {
                //当关灯时，选中的线变为白色，数字无变化
                findViewById<ImageView>(lineId).setImageResource(R.drawable.headline_off_selected)
            } else {
                //当开灯时，选中的线变为白亮色，数字变亮
                findViewById<ImageView>(lineId).setImageResource(R.drawable.headline_on)
                findViewById<ImageView>(numId).isSelected = true
                doAction(num_place)

            }
            binding.seekbar.progress = num_place
        }

        /**
         * seekbar点击事件，seekbar的长度正好对应10个挡位，每次切换直接结合selectState()传入固定参数实现换挡
         */
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (progress) {
                    0 -> {
                        selectState(R.id.line_sos, R.id.num_sos, 0)
                    }
                    1 -> {
                        selectState(R.id.line_0, R.id.num_0, 1)
                    }
                    2 -> {
                        selectState(R.id.line_1, R.id.num_1, 2)
                    }
                    3 -> {
                        selectState(R.id.line_2, R.id.num_2, 3)
                    }
                    4 -> {
                        selectState(R.id.line_3, R.id.num_3, 4)
                    }
                    5 -> {
                        selectState(R.id.line_4, R.id.num_4, 5)
                    }
                    6 -> {
                        selectState(R.id.line_5, R.id.num_5, 6)
                    }
                    7 -> {
                        selectState(R.id.line_6, R.id.num_6, 7)
                    }
                    8 -> {
                        selectState(R.id.line_7, R.id.num_7, 8)
                    }
                    9 -> {
                        selectState(R.id.line_8, R.id.num_8, 9)
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })



        binding.settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }


    //开启闪光灯
    fun turnOnFlashLight() {
        try {
            // 通过找到的后置摄像头ID，打开闪光灯
            cameraManager.setTorchMode(cameraId, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        runOnUiThread {
            binding.head.isSelected = true
            if (sharedPreferences.getBoolean("switch3",false)) {
                soundPool2.play(soundId2, 0.5f, 0.5f, 1, 0, 1f)
            }
        }
    }

    //关闭闪光灯
    fun turnOffFlashLight() {
        try {
            cameraManager.setTorchMode(cameraId, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        runOnUiThread {
            binding.head.isSelected = false
        }
    }

    var timer: Timer? = null

    private fun turnOnTaskSos() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 0, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 200, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 400, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 600, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 800, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 1000, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 1600, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 2200, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 2800, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 3400, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 4000, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 4600, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 5200, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 5400, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 5600, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 5800, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 6000, 7900)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 6200, 7900)
    }

    private fun turnOnTask0() {
        timer?.cancel()
        turnOnFlashLight()
    }

    private fun turnOnTask1() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 0, 2000)

        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 1000, 2000)
    }

    private fun turnOnTask2() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 0, 1500)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 750, 1500)
    }

    private fun turnOnTask3() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 0, 1000)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 500, 1000)
    }

    private fun turnOnTask4() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 0, 500)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 250, 500)
    }

    private fun turnOnTask5() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 0, 300)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 150, 300)
    }

    private fun turnOnTask6() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 0, 200)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 100, 200)
    }

    private fun turnOnTask7() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 0, 100)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 50, 100)
    }

    private fun turnOnTask8() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOnFlashLight()
            }
        }, 0, 20)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                turnOffFlashLight()
            }
        }, 10, 20)
    }

    private fun doAction(num_place: Int) {
        lifecycleScope.launch { // 在单独的协程中执行
            when (num_place) {
                0 -> {
                    turnOnTaskSos()
                }
                1 -> {
                    turnOnTask0()
                }
                2 -> {
                    turnOnTask1()
                }
                3 -> {
                    turnOnTask2()
                }
                4 -> {
                    turnOnTask3()
                }
                5 -> {
                    turnOnTask4()
                }
                6 -> {
                    turnOnTask5()
                }
                7 -> {
                    turnOnTask6()
                }
                8 -> {
                    turnOnTask7()
                }
                9 -> {
                    turnOnTask8()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //取消注册广播
        val lockScreenReceiver = LockScreenReceiver()
        unregisterReceiver(lockScreenReceiver)
        instance = null
    }

    fun lightOn(){
        binding.mainBtn.isSelected = true   //主按钮亮
        binding.head.isSelected = true      //顶部光亮
        binding.seekbar.isSelected = true   //滑块亮
        binding.offOn.text = "ON"           //开关下方字体更换
        isSwitchOff = false
    }

    fun lightOff(){
        binding.mainBtn.isSelected = false   //主按钮灭
        binding.head.isSelected = false      //顶部光灭
        binding.seekbar.isSelected = false   //滑块灭
        binding.offOn.text = "OFF"           //开关下方字体更换
        isSwitchOff = true
    }
}

/**
 * 下面这些是为了实现点击任何挡位都可以换挡，将每一个num和line都设置了点击事件
 * 不过在把seekbar范围扩大覆盖到整个挡位区域后就没有作用了
 */
//        再次封装使用方法
//        fun clickListener(lineId: Int, numId: Int, num_place: Int) {
//            findViewById<ImageView>(numId).setOnClickListener {
//                selectState(lineId, numId, num_place)
//            }
//
//            findViewById<ImageView>(lineId).setOnClickListener {
//                selectState(lineId, numId, num_place)
//            }
//
//        }

//        clickListener(R.id.line_sos, R.id.num_sos, 0)
//        clickListener(R.id.line_0, R.id.num_0, 1)
//        clickListener(R.id.line_1, R.id.num_1, 2)
//        clickListener(R.id.line_2, R.id.num_2, 3)
//        clickListener(R.id.line_3, R.id.num_3, 4)
//        clickListener(R.id.line_4, R.id.num_4, 5)
//        clickListener(R.id.line_5, R.id.num_5, 6)
//        clickListener(R.id.line_6, R.id.num_6, 7)
//        clickListener(R.id.line_7, R.id.num_7, 8)
//        clickListener(R.id.line_8, R.id.num_8, 9)