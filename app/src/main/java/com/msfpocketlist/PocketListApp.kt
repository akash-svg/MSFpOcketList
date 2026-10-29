package com.msfpocketlist

import android.app.Application
import com.msfpocketlist.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PocketListApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PocketListApp)
            modules(appModules)
        }
    }
}
