package a.co.varsitycollege.st10090538.opsc7311_poe

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode

class ExploreActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = true
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private var lastKnownLocation: Location? = null
    private val defaultLocation = LatLng(-26.195246, 28.034088)
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var currentPolyline: Polyline? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.explore)

        val observeIcon = findViewById<ImageView>(R.id.explore_observation_icon)
        val settingsIcon = findViewById<ImageView>(R.id.explore_settings_icon)
        val newObservation = findViewById<ImageView>(R.id.explore_newObservation_icon)

        observeIcon.setOnClickListener {
            startActivity(Intent(this, Observations::class.java))
        }
               settingsIcon.setOnClickListener {
            startActivity(Intent(this, Preferences::class.java))
        }

        newObservation.setOnClickListener {
            startActivity(Intent(this, NewObservation::class.java))
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getLocationPermission()
        getDeviceLocation(map!!)
        updateLocationUI()
        val startNavigationButton = findViewById<Button>(R.id.start_navigation_button)
        startNavigationButton.visibility = View.INVISIBLE
        startNavigationButton.isEnabled = false
        map?.setOnMarkerClickListener { marker ->
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.confirmation_dialog, null)
            builder.setView(dialogView)
            val alertDialog = builder.create()

            val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
            val confirmButton = dialogView.findViewById<Button>(R.id.confirm_button)
            val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)

            dialogMessage.text = "Navigate to " + marker.title + "?"

            confirmButton.setOnClickListener {
                alertDialog.dismiss()
                requestDirections(marker.position)
                val startNavigationButton = findViewById<Button>(R.id.start_navigation_button)
                startNavigationButton.visibility = View.VISIBLE
                startNavigationButton.isEnabled = true
                startNavigationButton.setOnClickListener {
                    val uri = Uri.parse("google.navigation:q=${marker?.position?.latitude},${marker?.position?.longitude}")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps") // Use Google Maps
                    startActivity(intent)
                }
            }

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()

            true // Consume the event
        }
    }

    private fun getMarkers(){
        val lat = lastKnownLocation!!.latitude
        val long = lastKnownLocation!!.longitude
        val webhelper = WebHelper().fetchHotspotData(long.toString(),
            lat.toString())
        webhelper.start()
        webhelper.join()

        for(element in GlobalData.hotspotList)
            map?.addMarker(
                MarkerOptions()
                    .position(LatLng(element.lat, element.log))
                    .title(element.name)
            )
    }
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation(googleMap: GoogleMap) {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), 15.toFloat()))
                        }
                        getMarkers()
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, 15.toFloat()))
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun requestDirections(selectedMarkerLatLng: LatLng) {
        val directionsApi = GeoApiContext.Builder()
            .apiKey("AIzaSyDrd4TRfKrboSdVS8C3SSXvmYiICStf3Q8")
            .build()

        val origin = "${lastKnownLocation!!.latitude}, ${lastKnownLocation!!.longitude}"
        val destination = "${selectedMarkerLatLng.latitude}, ${selectedMarkerLatLng.longitude}"

        DirectionsApi.newRequest(directionsApi)
            .origin(origin)
            .destination(destination)
            .mode(TravelMode.DRIVING)
            .setCallback(object : PendingResult.Callback<DirectionsResult?> {
                override fun onResult(result: DirectionsResult?) {
                    if (result != null && result.routes.isNotEmpty()) {
                        val decodedPath = decodePolyline(result.routes[0].overviewPolyline.decodePath())
                        runOnUiThread {
                            drawRouteOnMap(decodedPath)
                            findViewById<Button>(R.id.start_navigation_button).isEnabled = true
                        }
                    } else {
                    }
                }

                override fun onFailure(e: Throwable?) {
                }
            })
    }


    private fun decodePolyline(encodedPath: List<com.google.maps.model.LatLng>): List<LatLng> {
        val path = ArrayList<LatLng>()
        for (latLng in encodedPath) {
            path.add(LatLng(latLng.lat, latLng.lng))
        }
        return path
    }

    private fun drawRouteOnMap(path: List<LatLng>) {
        currentPolyline?.remove()
        val polylineOptions = PolylineOptions()
            .addAll(path)
            .width(8f)
            .color(Color.BLUE)
        currentPolyline = map!!.addPolyline(polylineOptions)
    }


}