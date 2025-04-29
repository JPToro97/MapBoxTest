package com.example.mapboxtest.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.mapboxtest.LocationHandler
import com.example.mapboxtest.MarkerManager
import com.example.mapboxtest.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

class MainActivity : ComponentActivity() {

    private lateinit var mapView: MapView
    private lateinit var pointAnnotationManager: PointAnnotationManager

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) setupMapUI()
        else Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            setupMapUI()
        }
        LocationHandler.centerOnDemand(mapView)
    }

    private fun setupMapUI() {
        val container = FrameLayout(this)
        mapView = MapView(this)

        val fab = FloatingActionButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_mylocation)
            setOnClickListener {
                LocationHandler.centerOnDemand(mapView)
            }
        }

        val fabParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            marginEnd = 32
            bottomMargin = 32
        }

        container.addView(mapView)
        container.addView(fab, fabParams)
        setContentView(container)

        mapView.mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
            val alertIcon = BitmapFactory.decodeResource(resources, R.drawable.warning)
            val normalIcon = BitmapFactory.decodeResource(resources, R.drawable.location_pin)
            style.addImage("alert-icon", alertIcon)
            style.addImage("normal-icon", normalIcon)

            pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

            pointAnnotationManager.addClickListener { annotation ->
                val name = annotation.getData()?.asJsonObject?.get("name")?.asString
                name?.let {
                    Toast.makeText(this, "Nombre: $it", Toast.LENGTH_SHORT).show()
                }
                true
            }

            mapView.mapboxMap.addOnMapClickListener { point ->
                MarkerManager.showMarkerTypeDialog(this, point, pointAnnotationManager)
                true
            }

            LocationHandler.enableUserLocationOnly(mapView)
        }
    }
}