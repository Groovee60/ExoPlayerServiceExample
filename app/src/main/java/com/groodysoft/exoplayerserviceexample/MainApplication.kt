package com.groodysoft.exoplayerserviceexample

import android.app.Application

class MainApplication : Application() {

    init {
        instance = this
    }

    companion object {
        var instance: MainApplication? = null


        val context : MainApplication
            get() {
                return instance as MainApplication
            }
    }
}