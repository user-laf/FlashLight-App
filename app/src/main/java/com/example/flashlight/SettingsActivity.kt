package com.example.flashlight

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
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
        binding.languageTitle.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }

        binding.removeSettings.setOnClickListener {
            // 在Activity代码中获取FragmentManager，然后调用beginTransaction()方法，创建一个新的Fragment事务
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            // 使用add()方法将Fragment添加到父容器中
            val myFragment = RemoveAdsFragment()
            fragmentTransaction.add(R.id.fragment_container, myFragment)
            // 将半透明View的可见性设置为VISIBLE，使其覆盖在Activity上
           binding.backgroundView.visibility = View.VISIBLE
            // 最后使用commit()方法提交事务，将Fragment添加到Activity中
            fragmentTransaction.commit()
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
