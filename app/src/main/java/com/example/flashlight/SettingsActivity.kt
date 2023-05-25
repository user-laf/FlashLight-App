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
        setContentView(R.layout.activity_settings)

        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        //通过键值对获取开关的布尔值
        binding.switch1.isChecked = sharedPreferences.getBoolean("switch1", false)
        binding.switch2.isChecked = sharedPreferences.getBoolean("switch2", false)
        binding.switch3.isChecked = sharedPreferences.getBoolean("switch3", false)

        binding.switch1.setOnCheckedChangeListener(this)
        binding.switch2.setOnCheckedChangeListener(this)
        binding.switch3.setOnCheckedChangeListener(this)

    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.switch1 -> sharedPreferences.edit().putBoolean("switch1", isChecked).apply()
            R.id.switch2 -> sharedPreferences.edit().putBoolean("switch2", isChecked).apply()
            R.id.switch3 -> sharedPreferences.edit().putBoolean("switch3", isChecked).apply()
        }

    }
}
