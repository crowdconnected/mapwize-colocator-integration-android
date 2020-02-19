package io.mapwize.mapwizeuicomponents

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    private var mapwizeAPIKey = "376d94542c92f13531f7268e877ace01"
    private var temporaryNewAPIKey = ""
    private var venueID = "5e2061c051eef50016a22b2c"

    private var colocatorAppKey = "qk65p7mf"
    private var colocatorEndPoint = "staging.colocator.net:443/socket"

    private val productionEndpoint = "production.colocator.net:443/socket"
    private val stagingEndpoint = "staging.colocator.net:443/socket"
    private val developmentEndpoint = "read-development.colocator.net:443/socket"

    var appKeyTextEdit: EditText? = null
    var colocatorServerTextView: TextView? = null
    var venueIDEditText: EditText? = null
    var mapwizeAPIKeyEditText: EditText? = null
    var updateAPIKeyButton: Button? = null

    var developmentButton: Button? = null
    var stagingButton: Button? = null
    var productionButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        var savedMapwizeAPIKey = sharedPref.getString("mapwize_api_key", null)
        if (savedMapwizeAPIKey != null) {
            mapwizeAPIKey = savedMapwizeAPIKey!!
        }

        appKeyTextEdit = rootView.findViewById(R.id.colocatorAppKeyEditText)
        colocatorServerTextView = rootView.findViewById(R.id.colocatorServerTextView)
        venueIDEditText = rootView.findViewById(R.id.venueIDEditText)
        mapwizeAPIKeyEditText = rootView.findViewById(R.id.mapwizeAPIKeyEditText)
        updateAPIKeyButton = rootView.findViewById(R.id.updateAPIKeyButton)

        developmentButton = rootView.findViewById(R.id.developmentButton)
        stagingButton = rootView.findViewById(R.id.stagingButton)
        productionButton = rootView.findViewById(R.id.productionButton)

        updateViewsWithListeners()
        updateViewsVithCredentials()

        return rootView
    }

    fun updateViewsWithListeners() {
        if(appKeyTextEdit != null) {
            appKeyTextEdit!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        if (s.length == 8) {
                            colocatorAppKey = s.toString()
                            credentialsHaveBeenUpdated()
                        }
                    }
                }
            })
        }

        if(venueIDEditText != null) {
            venueIDEditText!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        if (s.length > 10) {
                            venueID = s.toString()
                            credentialsHaveBeenUpdated()
                        }
                    }
                }
            })
        }

        if(mapwizeAPIKeyEditText != null) {
            mapwizeAPIKeyEditText!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        if (s.length > 15) {
                            temporaryNewAPIKey = s.toString()
                        }
                    }
                }
            })
        }

        if (developmentButton != null) {
            developmentButton!!.setOnClickListener(object: View.OnClickListener {
                override fun onClick(v: View?) {
                    colocatorEndPoint = developmentEndpoint
                    if (colocatorServerTextView != null) {
                        colocatorServerTextView!!.setText("DEVELOP")
                    }
                    credentialsHaveBeenUpdated()
                }
            })
        }

        if (stagingButton != null) {
            stagingButton!!.setOnClickListener(object: View.OnClickListener {
                override fun onClick(v: View?) {
                    colocatorEndPoint = stagingEndpoint
                    if (colocatorServerTextView != null) {
                        colocatorServerTextView!!.setText("STAGING")
                    }
                    credentialsHaveBeenUpdated()
                }
            })
        }

        if (productionButton != null) {
            productionButton!!.setOnClickListener(object: View.OnClickListener {
                override fun onClick(v: View?) {
                    colocatorEndPoint = productionEndpoint
                    if (colocatorServerTextView != null) {
                        colocatorServerTextView!!.setText("PRODUCTION")
                    }
                    credentialsHaveBeenUpdated()
                }
            })
        }

        if (updateAPIKeyButton != null) {
            updateAPIKeyButton!!.setOnClickListener(object: View.OnClickListener {
                override fun onClick(v: View?) {
                    if ( temporaryNewAPIKey.length < 15) {
                        Toast.makeText(context, "Mapwize API Key doesn't seem correct", Toast.LENGTH_SHORT).show()
                    } else {
                        mapwizeAPIKey = temporaryNewAPIKey
                        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                        sharedPref.edit().putString("mapwize_api_key", temporaryNewAPIKey).commit()
                        credentialsHaveBeenUpdated()

                        Toast.makeText(context, "API Key updated. Please restart the app", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    fun updateViewsVithCredentials() {
        if (appKeyTextEdit != null) {
            appKeyTextEdit!!.setText(colocatorAppKey)
        }

        if (colocatorServerTextView != null) {
            if (colocatorEndPoint.equals("staging.colocator.net:443/socket")) {
                colocatorServerTextView!!.setText("Staging")
            } else if (colocatorEndPoint.equals("read-development.colocator.net:443/socket")) {
                colocatorServerTextView!!.setText("Development")
            } else if (colocatorEndPoint.equals("production.colocator.net:443/socket")) {
                colocatorServerTextView!!.setText("Production")
            }
        }

        if (venueIDEditText != null) {
            venueIDEditText!!.setText(venueID)
        }

        if (mapwizeAPIKeyEditText != null) {
            mapwizeAPIKeyEditText!!.setText(mapwizeAPIKey)
        }
    }

    fun credentialsHaveBeenUpdated() {
        (activity as MainActivity).receivedNewSettings(colocatorAppKey, colocatorEndPoint, venueID, mapwizeAPIKey)
    }
}
