package io.mapwize.mapwizeuicomponents


import android.app.Application
import io.mapwize.mapwizeformapbox.AccountManager

class MapwizeApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        var APIKey = "YOUR_MAPWIZE_API_KEY"
        AccountManager.start(this, APIKey);
    }

}