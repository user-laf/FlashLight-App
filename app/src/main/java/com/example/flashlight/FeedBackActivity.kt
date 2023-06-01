package com.example.flashlight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.flashlight.databinding.ActivityFeedBackBinding

class FeedBackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFeedBackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.icReturn.setOnClickListener {
            finish()
        }

        binding.submit.setOnClickListener {
            if (binding.editText.text.toString().isEmpty()){
                Toast.makeText(this, "请输入信息!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText( this,"提交成功！", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}