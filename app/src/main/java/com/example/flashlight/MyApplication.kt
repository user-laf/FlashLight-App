package com.example.flashlight

import android.app.Activity
import android.app.Application
import android.os.Bundle

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(AppLifecycleCallbacks())
    }

    private inner class AppLifecycleCallbacks : ActivityLifecycleCallbacks {


        private var count = 0

        override fun onActivityCreated(p0: Activity, p1: Bundle?) {

        }

        override fun onActivityStarted(p0: Activity) {
            count++
        }

        override fun onActivityResumed(p0: Activity) {
        }

        override fun onActivityPaused(p0: Activity) {
        }

        override fun onActivityStopped(p0: Activity) {
            count--
            val homeActivity = HomeActivity.getInstance()
            if (count == 0) {
                    if (!homeActivity!!.sharedPreferences.getBoolean("switch2", false)){
                        if (!homeActivity.isSwitchOff) {
                            homeActivity.lightOff()
                            homeActivity.currentLine.setImageResource(R.drawable.headline_off_selected)//关灯时，被选中的line为白色
                            homeActivity.currentNum.isSelected = false                //关着时，被选中的num不亮
                            //关闭闪光灯
                            homeActivity.turnOffFlashLight()
                            homeActivity.timer?.cancel()
                        }
                    }

            }
        }

        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        }

        override fun onActivityDestroyed(p0: Activity) {
        }



    }



}
