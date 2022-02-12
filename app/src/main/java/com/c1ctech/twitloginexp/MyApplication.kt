package com.c1ctech.twitloginexp

import android.app.Application
import com.twitter.sdk.android.core.Twitter

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // Entry point to initialize the TwitterKit SDK.
        Twitter.initialize(this)

    }
}

