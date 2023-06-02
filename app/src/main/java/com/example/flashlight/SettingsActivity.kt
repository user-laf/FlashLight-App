package com.example.flashlight

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.preference.PreferenceManager
import com.example.flashlight.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        //通过键值对获取开关的布尔值
        binding.switch1.isChecked = sharedPreferences.getBoolean("switch1", true)
        binding.switch2.isChecked = sharedPreferences.getBoolean("switch2", true)
        binding.switch3.isChecked = sharedPreferences.getBoolean("switch3", true)

        binding.switch1.setOnCheckedChangeListener(this)
        binding.switch2.setOnCheckedChangeListener(this)
        binding.switch3.setOnCheckedChangeListener(this)

        binding.icReturn.setOnClickListener {
            finish()
        }


        //点击Language 进入语言设置界面
        binding.languageBtn.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }


        val dialog = Dialog(this,R.style.DialogStyle)
        dialog.setContentView(R.layout.fragment_remove_ads)
        val dialogWindow = dialog.window
        dialogWindow?.setDimAmount(0.5f)
        dialogWindow?.setGravity(Gravity.BOTTOM)
        val lp: WindowManager.LayoutParams = dialogWindow!!.attributes
        lp.y = 20
        dialogWindow.attributes = lp

        binding.removeBtn.setOnClickListener {
            dialog.show()
        }
        val finishDialog = dialog.findViewById<ImageView>(R.id.ad_finish)
        finishDialog.setOnClickListener {
            dialog.dismiss()
        }

        //分享按钮
        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            // 设置分享的数据类型
            intent.type = "text/plain"
            // 设置分享的标题
            intent.putExtra(Intent.EXTRA_TITLE, "分享手电筒")
            // 设置分享的内容
            intent.putExtra(Intent.EXTRA_TEXT, "这是我的手电筒")
            startActivity(Intent.createChooser(intent, "分享方式"))
        }

        binding.feedbackBtn.setOnClickListener {
            val intent = Intent(this, FeedBackActivity::class.java)
            startActivity(intent)
        }



    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.switch1 -> sharedPreferences.edit().putBoolean("switch1", isChecked).apply()
            R.id.switch2 -> sharedPreferences.edit().putBoolean("switch2", isChecked).apply()
            R.id.switch3 -> sharedPreferences.edit().putBoolean("switch3", isChecked).apply()
        }

    }
}
