package com.jidogoon.pdfredererview

import android.app.Application
import com.facebook.stetho.Stetho

/**
 * Created by jidogoon on 2018. 3. 18..
 */
class PdfViewApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}