package com.loopmoth.looplace

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var database: DatabaseReference
    //odwołanie do bazy danych

    private var AdventureListener: ValueEventListener? = null
    private val adventureArray: MutableList<Adventure> = mutableListOf()
    private val mMarkerArray = ArrayList<Marker>()

    private val PERMISSIONS_REQUEST = 100
    private var mMap: GoogleMap? = null
    private var locationManager : LocationManager? = null
    private var mapFragment: SupportMapFragment? = null
    private val mRotationMatrix = FloatArray(16)
    private var field: GeomagneticField? = null
    private lateinit var mSensorManager: SensorManager
    private var mRotation: Sensor? =null
    private var user: LatLng?=null
    private var locationSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish()
        }

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        }
        else{
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this,
                permissions,
                PERMISSIONS_REQUEST)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)


        database = FirebaseDatabase.getInstance().reference
    }

    override fun onResume() {
        super.onResume()
        //mRotation?.also { rotation ->
        //    mSensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_GAME)
        //}
        bShowAdv.setOnClickListener {

            bStart.visibility = Button.GONE
            val m1 = mMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(50.3875, 18.6308))
                    .anchor(0.5f, 0.5f)
                    .title("Title1")
                    .snippet("Snippet1")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            )

            bShowAdv.visibility = Button.GONE

        }

        val AdventureListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adventureArray.clear()
                dataSnapshot.children.mapNotNullTo(adventureArray) { it.getValue<Adventure>(Adventure::class.java) }
                //mapowanie z bazy do tablicy
                /*val newAdventure = database.child("adventures").push()
                val key = newAdventure.key
                Toast.makeText(this@MapsActivity, key, Toast.LENGTH_SHORT).show()*/


                adventureArray.forEach {
                    Toast.makeText(this@MapsActivity, it.key, Toast.LENGTH_SHORT).show()
                    //adventureTemp = Adventure(it.name, it.description, it.markers!!, it.key)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
                //jeżeli nie uda się połączyć z bazą
            }
        }
        database.child("adventures").addListenerForSingleValueEvent(AdventureListener)
    }



    override fun onPause() {
        super.onPause()
        //mSensorManager.unregisterListener(this)
    }

    public override fun onStop() {
        super.onStop()

        // Remove post value event listener
        AdventureListener?.let {
            database.removeEventListener(it)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.size == 1
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getLocation()

            mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment!!.getMapAsync(this)
        } else {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.setMinZoomPreference(15f)
        mMap!!.setMaxZoomPreference(19f)
        //mMap!!.getUiSettings().setScrollGesturesEnabled(false)

        getLocation()

        /*val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        if(hourOfDay>17 || hourOfDay < 7){
            mMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_night))
        }*/
    }

    fun getLocation(){
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 3f, locationListener)
        } catch(ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available");
        }

    }

    /*override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(
                mRotationMatrix, event.values
            )
            if(field!=null && mapFragment!=null && user!=null && locationSet){
                val orientation = FloatArray(3)
                SensorManager.getOrientation(mRotationMatrix, orientation)
                val bearing = Math.toDegrees(orientation[0].toDouble()) + field!!.declination
                val cameraPosition = CameraPosition.Builder().target(user!!).bearing(bearing.toFloat()).build()
                mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                //Toast.makeText(this@MapsActivity, "działa", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    //override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    //}

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            field = GeomagneticField(
                location.getLatitude().toFloat(),
                location.getLongitude().toFloat(),
                location.getAltitude().toFloat(),
                System.currentTimeMillis()
            )

            user = LatLng(location.latitude, location.longitude)
            if(mapFragment!=null){
                Toast.makeText(this@MapsActivity, location.longitude.toString() + " " + location.latitude.toString(), Toast.LENGTH_SHORT).show()
                mMap!!.clear()
                mMap!!.addMarker(MarkerOptions().position(user!!).title("Tu jesteś").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                if(bShowAdv.visibility==Button.GONE) {
                    // jeśli zmieni się położenie użytkownika a mają być pokazane trasy, to ma je pokazać
                    val m1 = mMap!!.addMarker(
                        MarkerOptions()
                            .position(LatLng(50.3875, 18.6308))
                            .anchor(0.5f, 0.5f)
                            .title("Title1")
                            .snippet("Snippet1")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    )
                }
                val cameraPosition = CameraPosition.Builder()
                    .target(user)
                    .build()
                mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                locationSet=true
            }
        }

        fun ShowAdventures()
        {


        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}
