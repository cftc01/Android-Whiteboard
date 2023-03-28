package com.cascade.whiteboard

import android.app.Application
import com.cascade.whiteboard.api.CronetInstance

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        CronetInstance.setup(this.applicationContext)
    }
}
