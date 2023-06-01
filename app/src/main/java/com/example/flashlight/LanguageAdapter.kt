package com.example.flashlight

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LanguageAdapter(private val languageList: List<String>) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {


    companion object {
        const val SELECTED_LANGUAGE_KEY = "selected_language_key"
    }

    var selectedLanguageIndex = 0

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val languageName: TextView = view.findViewById(R.id.languageName)
        val selectLanguage: ImageView = view.findViewById(R.id.select_language)

        init {
            selectLanguage.visibility = if (adapterPosition == 0) View.VISIBLE else View.GONE
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val previousIndex = selectedLanguageIndex
            selectedLanguageIndex = adapterPosition
            notifyItemChanged(previousIndex)
            notifyItemChanged(selectedLanguageIndex)

            val sharedPreferences =
                v.context.getSharedPreferences("language_pref", Context.MODE_PRIVATE)
            sharedPreferences.edit().putInt(SELECTED_LANGUAGE_KEY, selectedLanguageIndex).apply()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.languageName.text = languageList[position]
        holder.selectLanguage.visibility =
            if (position == selectedLanguageIndex) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = languageList.size
}
