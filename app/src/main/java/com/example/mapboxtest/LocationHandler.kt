package com.example.mapboxtest


import android.content.Context
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.locationcomponent.location

object LocationHandler {

    fun enableUserLocationOnly(mapView: MapView) {
        mapView.location.updateSettings {
            enabled = true
            pulsingEnabled = true
        }
    }


    fun centerOnDemand(mapView: MapView) {
        val plugin = mapView.location

        val listener = object : (Point) -> Unit {
            override fun invoke(point: Point) {
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(point)
                        .zoom(14.0)
                        .build()
                )
                // Remover listener después del primer centrado
                plugin.removeOnIndicatorPositionChangedListener(this)
            }
        }

        plugin.addOnIndicatorPositionChangedListener(listener)
    }
}
