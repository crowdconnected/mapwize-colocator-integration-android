package io.mapwize.mapwizeuicomponents


import android.app.Application
import io.mapwize.mapwizeformapbox.AccountManager

class MapwizeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AccountManager.start(this, "7b63e66f6a5bebaea25fc5a0e276d5ec")
    }

}