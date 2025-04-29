package com.example.mapboxtest

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.gson.JsonParser
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions

object MarkerManager {

    fun showMarkerTypeDialog(
        context: Context,
        point: Point,
        pointAnnotationManager: PointAnnotationManager
    ) {
        AlertDialog.Builder(context)
            .setTitle("Agregar punto")
            .setMessage("¿Qué tipo de punto deseas agregar?")
            .setPositiveButton("Normal") { _, _ ->
                showNameInputDialog(context, point, false, pointAnnotationManager)
            }
            .setNegativeButton("Alerta") { _, _ ->
                showNameInputDialog(context, point, true, pointAnnotationManager)
            }
            .show()
    }

    private fun showNameInputDialog(
        context: Context,
        point: Point,
        isAlert: Boolean,
        manager: PointAnnotationManager
    ) {
        val input = EditText(context).apply {
            hint = "Escribe un nombre"
        }

        AlertDialog.Builder(context)
            .setTitle("Nombre del punto")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val name = input.text.toString().ifEmpty { "Sin nombre" }
                createMarker(point, name, isAlert, manager)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun createMarker(
        point: Point,
        name: String,
        isAlert: Boolean,
        manager: PointAnnotationManager
    ) {
        val iconId = if (isAlert) "alert-icon" else "normal-icon"
        val data = JsonParser.parseString("""{"name": "$name"}""")

        val options = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(iconId)
            .withData(data)

        manager.create(options)
    }
}