package com.mytest.mise

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import java.lang.Exception

class LocationProvider (val context:Context){
    private var location : Location? = null
    private var locationManager : LocationManager? = null
    init {
        getLocation()
    }

    private fun getLocation(){
        try{
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var gpsLocation : Location? = null
            var networkLocation : Location? = null

            val isGpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if(!isGpsEnabled && !isNetworkEnabled) return

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            if(isGpsEnabled){
                gpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if(isNetworkEnabled){
                networkLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            if(gpsLocation!=null && networkLocation!=null){
                if(gpsLocation.accuracy>networkLocation.accuracy) location = gpsLocation
                else location = networkLocation
            }else if(gpsLocation!= null){
                location = gpsLocation
            }else if(networkLocation!=null){
                location = networkLocation
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }
    fun getLatitude() : Double{
        return location?.latitude ?: 0.0

    }
    fun getLongitude() : Double{
        return location?.longitude ?: 0.0
    }

}