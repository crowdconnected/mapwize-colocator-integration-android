package io.mapwize.mapwizeuicomponents

import android.app.Application
import io.mapwize.mapwizeformapbox.AccountManager

class MapwizeApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        var APIKey = "376d94542c92f13531f7268e877ace01"
        AccountManager.start(this, APIKey)
    }

    companion object {
        fun startwithAPIKey(apiKey: String) {
            AccountManager.start(this, apiKey)
        }
    }

}