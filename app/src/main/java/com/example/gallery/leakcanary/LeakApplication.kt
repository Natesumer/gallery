package com.example.gallery.leakcanary

import android.app.Application
import leakcanary.LeakCanary

class LeakApplication:Application() {
    override fun onCreate() {
        super.onCreate()

            //App 处于前台时检测保留对象的阈值，默认是 5
            LeakCanary.config=LeakCanary.config.copy(retainedVisibleThreshold = 4)
            //自定义要检测的保留对象类型，默认监测 Activity，Fragment，FragmentViews 和 ViewModels
//            AppWatcher.config= AppWatcher.config.copy(watchFragmentViews = false)
            //隐藏泄漏显示活动启动器图标，默认为 true
            LeakCanary.showLeakDisplayActivityLauncherIcon(false)


    }
}