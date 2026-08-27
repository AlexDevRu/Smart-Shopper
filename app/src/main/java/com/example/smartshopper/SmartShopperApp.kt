package com.example.smartshopper

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartShopperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        Firebase.initialize(context = this)

        // Configure App Check to use the debug provider factory for local development
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )
    }
}
