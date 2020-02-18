package io.mapwize.mapwizeuicomponents

import android.app.Application
import android.preference.PreferenceManager
import io.mapwize.mapwizeformapbox.AccountManager

class MapwizeApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        var APIKey = "376d94542c92f13531f7268e877ace01"

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        var savedMapwizeAPIKey = sharedPref.getString("mapwize_api_key", null)

        if (savedMapwizeAPIKey == null) {
            sharedPref.edit().putString("mapwize_api_key", APIKey).commit()
            AccountManager.start(this, APIKey)
        } else {
            AccountManager.start(this, savedMapwizeAPIKey)
        }
    }
}