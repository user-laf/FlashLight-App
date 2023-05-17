package com.example.flashlight


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment


class RemoveAdsFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_remove_ads, container, false)

        val finishBtn = view?.findViewById<ImageView>(R.id.ad_finish)
        finishBtn?.setOnClickListener {
            // 获取FragmentManager，然后调用beginTransaction()方法，创建一个新的Fragment事务
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            // 使用remove()方法将当前Fragment从父容器中移除
            fragmentTransaction.remove(this@RemoveAdsFragment)

            // 将半透明View的可见性设置为GONE，隐藏背景遮罩
            val backgroundView = requireActivity().findViewById<View>(R.id.background_view)
            backgroundView.visibility = View.GONE

            // 最后使用commit()方法提交事务，将Fragment从Activity中移除
            fragmentTransaction.commit()
        }
        return view

    }

}