package com.example.flashlight

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LanguageAdapter(val languageList: List<String>) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val languageName: TextView = view.findViewById(R.id.languageName)
        val selectLanguage : ImageView = view.findViewById(R.id.select_language)

        init {
            itemView.setOnClickListener {
                // 隐藏上一个被选中的selectLanguage
                lastSelectedViewHolder?.selectLanguage?.visibility = View.GONE
                // 设置当前选中的selectLanguage可见
                selectLanguage.visibility = View.VISIBLE
                // 更新lastSelectedViewHolder
                lastSelectedViewHolder = this
            }
        }
    }
    // 保存当前被选中的ViewHolder引用
    companion object {
        @SuppressLint("StaticFieldLeak")
        var lastSelectedViewHolder: ViewHolder? = null
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.languageName.text = languageList[position]
        if (position == 0 && lastSelectedViewHolder == null) {
            // 初始状态下，默认选中第一个列表项
            holder.selectLanguage.visibility = View.VISIBLE
            lastSelectedViewHolder = holder
        } else {
            // 仅设置当前选中的selectLanguage可见
            holder.selectLanguage.visibility =
                if (holder == lastSelectedViewHolder) View.VISIBLE else View.GONE
        }
    }
    override fun getItemCount() = languageList.size

}
