package io.mapwize.mapwizeuicomponents

import android.Manifest
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.mapwize.mapwizecomponents.ui.MapwizeFragment
import io.mapwize.mapwizecomponents.ui.MapwizeFragmentUISettings
import io.mapwize.mapwizeformapbox.api.MapwizeObject
import io.mapwize.mapwizeformapbox.api.Place
import io.mapwize.mapwizeformapbox.map.MapOptions
import io.mapwize.mapwizeformapbox.map.MapwizePlugin
import net.crowdconnected.androidcolocator.CoLocator
import androidx.core.app.*
import java.time.Duration

class MainActivity : AppCompatActivity(), MapwizeFragment.OnFragmentInteractionListener {

    private var settingsFragment: SettingsFragment? = null
    private var mapboxMap: MapboxMap? = null
    private var mapwizePlugin: MapwizePlugin? = null
    private var locationProvider: ColocatorIndoorLocationProvider? = null

    private var settingsToggled = false;

    private var mapwizeAPIKey = "376d94542c92f13531f7268e877ace01"
    private var venueID = ""
    private var mapOptions: MapOptions? = null
    private var mapUISettings: MapwizeFragmentUISettings? = null

    private var colocatorAppKey = ""
    private var colocatorEndPoint = "staging.colocator.net:443/socket"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        colocatorAppKey = sharedPref.getString("colocator_app_key", "")
        venueID = sharedPref.getString("mapwize_venue_id", "")

        settingsFragment = SettingsFragment()

        setupSettingsForColocator()
        setupSettingsForMap()
        displayMapFragment()

        settingsToggled = false
    }

    private fun setupSettingsForColocator() {
        if (CoLocator.instance() != null) {
            CoLocator.instance().stop()
            Log.e("DEV","Colocator stopped ")
        }
        if (colocatorAppKey.isEmpty()) {
            Toast.makeText(applicationContext, "Set an App Key for Colocator", Toast.LENGTH_LONG)
            return
        }

        CoLocator.start(this.application, colocatorAppKey, colocatorEndPoint)
    }

    private fun setupSettingsForMap() {
        mapOptions = MapOptions.Builder()
                .centerOnVenue(venueID)
                .build()

        mapUISettings = MapwizeFragmentUISettings.Builder()
                .menuButtonHidden(true)
                .floorControllerHidden(true)
                .compassHidden(true)
                .build()
    }

    private fun displayMapFragment() {
        val mapwizeFragment = MapwizeFragment.newInstance(mapOptions!!, mapUISettings!!)
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()

        transaction.replace(R.id.fragment_container, mapwizeFragment)
        transaction.addToBackStack(null)

        transaction.commit()
    }

    private fun displaySettingsFragment() {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()

        transaction.replace(R.id.fragment_container,settingsFragment!!)
        transaction.addToBackStack(null)

        transaction.commit()
    }

    fun  toggleSettings(view: View) {
        if (settingsToggled) {
            setupSettingsForColocator()
            setupSettingsForMap()
            displayMapFragment()
        } else {
            settingsFragment!!.updateViewsWithCredentials()
            displaySettingsFragment()
        }

        settingsToggled = !settingsToggled
    }

    fun receivedNewSettings(appKey: String, serverEndPoint: String, venueID: String, apiKey: String) {
        this.mapwizeAPIKey = apiKey
        this.venueID = venueID
        this.colocatorAppKey = appKey
        this.colocatorEndPoint = serverEndPoint
    }

    fun setupDeviceID(view: View) {
        if (CoLocator.instance() == null) {
            return
        }
        var id = CoLocator.instance().deviceId
        if (settingsToggled && settingsFragment != null) {
            settingsFragment!!.updateDeviceID(id)
        }
    }

    /**
     * Fragment listener
     */
    override fun onFragmentReady(mapboxMap: MapboxMap?, mapwizePlugin: MapwizePlugin?) {
        this.mapboxMap = mapboxMap
        this.mapwizePlugin = mapwizePlugin
        this.locationProvider = ColocatorIndoorLocationProvider(this)
        this.mapwizePlugin?.setLocationProvider(this.locationProvider!!)
        this.locationProvider!!.start()
        this.mapboxMap?.locationComponent?.renderMode = RenderMode.NORMAL
    }

    override fun onMenuButtonClick() {
    }

    override fun onInformationButtonClick(mapwizeObject: MapwizeObject?) {
    }

    override fun onFollowUserButtonClickWithoutLocation() {
    }

    override fun shouldDisplayInformationButton(mapwizeObject: MapwizeObject?): Boolean {
        when (mapwizeObject) {
            is Place -> return true
        }
        return false
    }

    override fun shouldDisplayFloorController(floors: MutableList<Double>?): Boolean {
        if (floors == null || floors.size <= 1) {
            return false
        }
        return true
    }
}
