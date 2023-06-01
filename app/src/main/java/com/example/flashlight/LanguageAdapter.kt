package com.example.flashlight

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LanguageAdapter(val languageList: List<String>, val sharedPrefs: SharedPreferences) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private var selectedPosition = sharedPrefs.getInt("selected_position", 0)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val languageName: TextView = view.findViewById(R.id.languageName)
        val selectLanguage: ImageView = view.findViewById(R.id.select_language)

        var showSelectLanguage: Boolean = false
            set(value) {
                field = value
                selectLanguage.visibility = if (field) View.VISIBLE else View.GONE
            }

        init {
            // 点击语言名称时显示或隐藏 selectLanguage
            itemView.setOnClickListener {
                selectedPosition = adapterPosition
                notifyDataSetChanged() // 刷新列表
                sharedPrefs.edit().putInt("selected_position", selectedPosition).apply()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.languageName.text = languageList[position]
        holder.showSelectLanguage = position == selectedPosition
    }

    override fun getItemCount() = languageList.size

    fun restoreSelection() {
        var lastIndex = -1
        languageList.forEachIndexed { index, language ->
            if (index == selectedPosition) {
                lastIndex = index
            }
        }
        selectedPosition = lastIndex
        notifyItemChanged(selectedPosition)
    }
}
