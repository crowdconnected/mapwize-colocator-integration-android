package io.mapwize.mapwizeuicomponents

import android.Manifest
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.mapwize.mapwizecomponents.ui.MapwizeFragment
import io.mapwize.mapwizecomponents.ui.MapwizeFragmentUISettings
import io.mapwize.mapwizeformapbox.api.MapwizeObject
import io.mapwize.mapwizeformapbox.api.Place
import io.mapwize.mapwizeformapbox.map.MapOptions
import io.mapwize.mapwizeformapbox.map.MapwizePlugin
import kotlinx.android.synthetic.main.activity_main.*
import net.crowdconnected.androidcolocator.CoLocator

class MainActivity : AppCompatActivity(), MapwizeFragment.OnFragmentInteractionListener {

    private var mapwizeFragment: MapwizeFragment? = null
    private var mapboxMap: MapboxMap? = null
    private var mapwizePlugin: MapwizePlugin? = null
    private var locationProvider: ColocatorIndoorLocationProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        val endpoint = "YOUR_CC_APP_KEY.colocator.net:443/socket"
        val appKey = "YOUR_CC_APP_KEY"
        CoLocator.init(this.application, endpoint, appKey)

        var organisationID = "YOUR_ORGANISATION_ID"
        val venueID = "YOUR_VENUE_ID"
        val opts = MapOptions.Builder()
                .restrictContentToOrganization(organisationID)
                .centerOnVenue(venueID)
                .build()

        var uiSettings = MapwizeFragmentUISettings.Builder()
                .menuButtonHidden(true)
                .floorControllerHidden(true)
                .compassHidden(true)
                .build()

        this.mapwizeFragment = MapwizeFragment.newInstance(opts, uiSettings)
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.add(fragmentContainer.id, mapwizeFragment!!)
        ft.commit()
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
