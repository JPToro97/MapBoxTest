package com.example.mapboxtest.ui

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.mapboxtest.R
import com.google.gson.JsonParser
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener

class MainActivity : ComponentActivity() {

    private lateinit var mapView: MapView
    private lateinit var pointAnnotationManager: PointAnnotationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView = MapView(this)
        setContentView(mapView)

        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-98.0, 39.5))
                .zoom(3.0)
                .build()
        )

        mapView.mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->

            // Cargar ícono personalizado de alerta
            val alertBitmap = BitmapFactory.decodeResource(resources, R.drawable.warning)
            if (alertBitmap != null) {
                style.addImage("alert-icon", alertBitmap)
                style.addImage("normal-icon", alertBitmap)
            } else {
                Log.e("MainActivity", "No se pudo cargar el ícono alert_icon.png")
            }

            // Inicializar el administrador de anotaciones
            val annotationPlugin = mapView.annotations
            pointAnnotationManager = annotationPlugin.createPointAnnotationManager()

            pointAnnotationManager.addClickListener { annotation ->
                val name = annotation.getData()?.asJsonObject?.get("name")?.asString
                if (name != null) {
                    Toast.makeText(this, "Nombre: $name", Toast.LENGTH_SHORT).show()
                }
                true
            }

            // Listener para agregar un marcador según la selección
            mapView.mapboxMap.addOnMapClickListener { point ->
                showMarkerTypeDialog(point)
                true
            }
        }
    }

    private fun showMarkerTypeDialog(point: Point) {
        AlertDialog.Builder(this)
            .setTitle("Agregar punto")
            .setMessage("¿Qué tipo de punto deseas agregar?")
            .setPositiveButton("Normal") { _, _ ->
                showNameInputDialog(point, isAlert = false)
            }
            .setNegativeButton("Alerta") { _, _ ->
                showNameInputDialog(point, isAlert = true)
            }
            .show()
    }

    // NUEVO: Dialog para pedir el nombre
    private fun showNameInputDialog(point: Point, isAlert: Boolean) {
        val input = EditText(this)
        input.hint = "Escribe un nombre para el punto"

        AlertDialog.Builder(this)
            .setTitle("Nombre del punto")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val name = input.text.toString().ifEmpty { "Sin nombre" }
                if (isAlert) {
                    addAlertMarker(point, name)
                } else {
                    addNormalMarker(point, name)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun addNormalMarker(point: Point, name: String) {
        val options = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage("normal-icon")
            .withData(JsonParser.parseString("""{"name": "$name"}"""))
        pointAnnotationManager.create(options)
    }

    private fun addAlertMarker(point: Point, name: String) {
        val options = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage("alert-icon")
            .withData(JsonParser.parseString("""{"name": "$name"}"""))
        pointAnnotationManager.create(options)
    }

}