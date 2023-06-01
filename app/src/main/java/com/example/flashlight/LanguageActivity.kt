package com.example.flashlight

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashlight.databinding.ActivityLanguageBinding


class LanguageActivity : AppCompatActivity(){

    private val languageList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLanguages()


        binding.languageRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = LanguageAdapter(languageList)
        binding.languageRecyclerView.adapter = adapter

        binding.icReturn.setOnClickListener {
            finish()
        }

    }

    private fun initLanguages(){
        languageList.add("English")
        languageList.add("Português")
        languageList.add("pycckий")
        languageList.add("Deutsch")
        languageList.add("繁體中文")
        languageList.add("简体中文")
        languageList.add("Français")
        languageList.add("Español")
        languageList.add("Italiano")
        languageList.add("한국어")
    }


}