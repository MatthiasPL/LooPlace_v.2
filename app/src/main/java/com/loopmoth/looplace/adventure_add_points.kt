package com.loopmoth.looplace

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class adventure_add_points : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var long: Double? = 0.0
    private var lat: Double? = 0.0
    //współrzędne użytkownika

    private lateinit var name: String
    private lateinit var desc: String
    //dane z formularza wyżej

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure_add_points)

        name = intent.getStringExtra("name")
        desc = intent.getStringExtra("desc")
        //przesłane dane z poprzedniego formularza

        //Toast.makeText(this@adventure_add_points, name+desc, Toast.LENGTH_LONG).show()
        //do sprawdzenia czy przesyłanie danych pomiędzy aktywnościami jest poprawne

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //do sprawdzenia ostatniej lokalizacji użytkownika, aby otworzyć mapę w jego lokalizacji

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //wczytanie fragmentu mapy
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //odniesienie do mapy, aby można było w łatwy sposób odwoływać się do mapy
        obtainLocation()
        //pobranie lokalizacji, przeniesienie tam widoku i prybliżenie
    }

    @SuppressLint("MissingPermission")
    private fun obtainLocation(){
        //pobieranie lokalizacji użytkownika
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                lat =  location?.latitude
                long = location?.longitude

                val currentLocation = LatLng(lat!!, long!!)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16.0f))
                //newLatLng - też poprawna funkcja, ale bez możliwości przybliżania
            }
    }
}
