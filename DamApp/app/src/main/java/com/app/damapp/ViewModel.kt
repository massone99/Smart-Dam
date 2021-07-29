package com.app.damapp

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewModel(context: Context) {

    private val TAG = "ViewModel"

    // Instantiate the RequestQueue
    private val queue = Volley.newRequestQueue(context)

    private var status:String = "ND"
    private var water:Float = 0.0f

    var bluetoothStatus: String? = "ND"
        get() = field
        set(value) {
            field = value
        }
    /**
     * A function to retrieve the current state of the Dam
     */
    fun retrieveDamStatus() {
        val url = "https://c252913b0545.ngrok.io/api/status"


        val statusRequest = StringRequest(Request.Method.GET, url,
            { response ->
                status = response

                if (status != "null") {
                    status = status.substring(1, status.length-1)
                }

                // Log.d(TAG, "HTTP GET Request went OK")
            },
            { error ->
                status = "ERROR"

                Log.d(TAG, error.toString())
            }
        )
        queue.add(statusRequest)
    }

    /**
     * Returns the last status received from the backend
     */
    fun getDamState(): String {
        return status;
    }

    /**
     * A function to retrieve the current level of the Dam
     */
    fun retrieveDamLevel() {
        // todo: implement method
    }

    /**
     * A function to retrieve the current water level of the Dam
     */
    fun retrieveWaterLevel() {
        val url = "https://c252913b0545.ngrok.io/api/water"

        val waterRequest = StringRequest(Request.Method.GET, url,
            { response ->
                water = response.toFloat()

                // Log.d(TAG, "HTTP GET Request went OK")
            },
            { error ->
                water = 0F

                 Log.d(TAG, error.toString())
            }
        )
        queue.add(waterRequest)
    }

    /**
     * Returns the last water level
     */
    fun getWaterLevel(): Float {
        return water;
    }

    /**
     * A simple coroutine to update data in a background thread
     */
    fun updateData() {
        GlobalScope.launch(Dispatchers.Default) {
            retrieveDamStatus()
            retrieveWaterLevel()
        }
    }

}