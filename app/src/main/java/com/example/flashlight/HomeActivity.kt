package com.example.flashlight


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Vibrator
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.flashlight.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var soundPool1: SoundPool = SoundPool.Builder().build()
    private var soundPool2: SoundPool = SoundPool.Builder().build()
    private var soundId1: Int = 0
    private var soundId2: Int = 0
    private lateinit var vibrator: Vibrator
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String//定义变量用来存储后置摄像头ID
    var isSwitchOff: Boolean = false


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


        try {
            // 通过找到的后置摄像头ID，打开闪光灯
            cameraManager.setTorchMode(cameraId, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        binding.mainBtn.isSelected = true   //主按钮亮
        binding.head.isSelected = true      //顶部光亮
        binding.seekbar.isSelected = true   //滑块亮
        binding.line0.setImageResource(R.drawable.headline_on)
        binding.num0.isSelected = true
        binding.offOn.text = "ON"
        isSwitchOff = false

        soundId1 = soundPool1.load(this, R.raw.click1, 1)
        soundId2 = soundPool2.load(this, R.raw.light4, 1)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        /************************************************************************/
        //主开关点击事件
        binding.mainBtn.setOnClickListener {
            //震动
            vibrator.vibrate(50)
            //音效
//            MediaPlayer.create(this, R.raw.click1).start()
            soundPool1.play(soundId1, 0.5f, 0.5f, 1, 0, 1f)
            //通过滑块的位置寻找到线和数字的位置
            val layoutParams = binding.headBtn.layoutParams as ConstraintLayout.LayoutParams
            val line = findViewById<ImageView>(layoutParams.startToStart)
            val num = findViewById<ImageView>(layoutParams.endToEnd)
            val num_switch = binding.seekbar.progress

            if (isSwitchOff) {
                binding.mainBtn.isSelected = true   //主按钮亮
                binding.head.isSelected = true      //顶部光亮
                binding.seekbar.isSelected = true   //滑块亮
                binding.offOn.text = "ON"           //开关下方字体更换
                line.setImageResource(R.drawable.headline_on) //开灯时，被选中的line白亮光
                num.isSelected = true               //开灯时，被选中的num亮

                //开灯时根据当前seekBar位置自动选择频率
                doAction(num_switch)
                isSwitchOff = false
            } else {
                binding.mainBtn.isSelected = false    //主按钮灭
                binding.head.isSelected = false       //顶部光灭
                binding.seekbar.isSelected = false    //滑块灭
                binding.offOn.text = "OFF"            //开关下方字体更换
                line.setImageResource(R.drawable.headline_off_selected)//关灯时，被选中的line为白色
                num.isSelected = false                //关着时，被选中的num不亮

                //关闭闪光灯
                turnOffFlashLight()
                timer?.cancel()
                isSwitchOff = true
            }
        }

        /*********************************************************/
        //封装点击事件
        fun SelectState(lineId: Int, numId: Int, num_place: Int) {
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


        //再次封装使用方法
        fun clickListener(lineId: Int, numId: Int, num_place: Int) {
            findViewById<ImageView>(numId).setOnClickListener {
                SelectState(lineId, numId, num_place)
            }

            findViewById<ImageView>(lineId).setOnClickListener {
                SelectState(lineId, numId, num_place)
            }

        }

        clickListener(R.id.line_sos, R.id.num_sos, 0)
        clickListener(R.id.line_0, R.id.num_0, 1)
        clickListener(R.id.line_1, R.id.num_1, 2)
        clickListener(R.id.line_2, R.id.num_2, 3)
        clickListener(R.id.line_3, R.id.num_3, 4)
        clickListener(R.id.line_4, R.id.num_4, 5)
        clickListener(R.id.line_5, R.id.num_5, 6)
        clickListener(R.id.line_6, R.id.num_6, 7)
        clickListener(R.id.line_7, R.id.num_7, 8)
        clickListener(R.id.line_8, R.id.num_8, 9)

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (progress) {
                    0 -> {
                        SelectState(R.id.line_sos, R.id.num_sos, 0)
                    }
                    1 -> {
                        SelectState(R.id.line_0, R.id.num_0, 1)
                    }
                    2 -> {
                        SelectState(R.id.line_1, R.id.num_1, 2)
                    }
                    3 -> {
                        SelectState(R.id.line_2, R.id.num_2, 3)
                    }
                    4 -> {
                        SelectState(R.id.line_3, R.id.num_3, 4)
                    }
                    5 -> {
                        SelectState(R.id.line_4, R.id.num_4, 5)
                    }
                    6 -> {
                        SelectState(R.id.line_5, R.id.num_5, 6)
                    }
                    7 -> {
                        SelectState(R.id.line_6, R.id.num_6, 7)
                    }
                    8 -> {
                        SelectState(R.id.line_7, R.id.num_7, 8)
                    }
                    9 -> {
                        SelectState(R.id.line_8, R.id.num_8, 9)
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
            soundPool2.play(soundId2, 0.5f, 0.5f, 1, 0, 1f)
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
}