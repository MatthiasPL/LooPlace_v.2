package com.loopmoth.looplace

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.*

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
    private var MarkerListener: ValueEventListener? = null
    private val adventureArray: MutableList<Adventure> = mutableListOf()
    private val mArray: MutableList<CMarker> = mutableListOf()
    private val mMarkerArray = ArrayList<Marker>()
    private val markersArray: MutableList<Marker> = mutableListOf()
    private var yourmarker: Marker? = null
    private var advlat:Double?=null
    private var advlong:Double?=null
    private var advname:String?=null
    private var advsnippet:String?=null
    private var navigationflag=false

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
        database = FirebaseDatabase.getInstance().reference
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



    }

    override fun onResume() {
        super.onResume()
        //mRotation?.also { rotation ->
        //    mSensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_GAME)
        //}
        bShowAdv.setOnClickListener {

            bStart.visibility = Button.GONE
            mMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(50.3875, 18.6308))
                    .anchor(0.5f, 0.5f)
                    .title("Title1")
                    .snippet("Snippet1")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            )

            bShowAdv.visibility = Button.GONE
            //GetPoints()
            //initAdventure()

        }

        bStart.setOnClickListener {
            //po kliknięciu startu włącza się aplikacja map, która nawiguje nas do pierwszego punktu przygody
            //podosimy flagę, że użytkownik wybrał trasę i nawiguje się do punktu
            navigationflag=true;
            val gmmIntentUri = Uri.parse("google.navigation:q="+ advlat+","+advlong);
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    private fun GetFullAdventure(){
        if(navigationflag)
        {
            
        }
    }

    /*private fun GetPoints() {
        val AdventureListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adventureArray.clear()
                dataSnapshot.children.mapNotNullTo(adventureArray) { it.getValue<Adventure>(Adventure::class.java) }
                //mapowanie z bazy do tablicy
                /*val newAdventure = database.child("adventures").push()
                val key = newAdventure.key
                Toast.makeText(this@MapsActivity, key, Toast.LENGTH_SHORT).show()*/

                val advArray: MutableList<String> = mutableListOf()

                adventureArray.forEach{
                    advArray.add(it.name + "\n" + it.description + "\nLiczba punktów: " + it.markers!!.size.toString())
                }
                Toast.makeText(this@MapsActivity, advArray.toString(), Toast.LENGTH_SHORT).show()
                /*adventureArray.forEach {
                    GetMarkers()
                    //Toast.makeText(this@MapsActivity, it.key, Toast.LENGTH_SHORT).show()
                    //adventureTemp = Adventure(it.name, it.description, it.markers!!, it.key)
                }*/
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
                //jeżeli nie uda się połączyć z bazą
            }
        }
        database.child("adventures").addListenerForSingleValueEvent(AdventureListener)
    }

    private fun GetMarkers() {
        val MarkerListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mArray.clear()
                dataSnapshot.children.mapNotNullTo(mArray) { it.getValue<CMarker>(CMarker::class.java) }
                //mapowanie z bazy do tablicy
                /*val newAdventure = database.child("adventures").push()
                val key = newAdventure.key
                Toast.makeText(this@MapsActivity, key, Toast.LENGTH_SHORT).show()*/
                val mark=mArray[0]
                mMap!!.addMarker(
                    MarkerOptions()
                        .position(LatLng(mark.latitude!!, mark.longitude!!))
                        .anchor(0.5f, 0.5f)
                        .title(mark.title)
                        .snippet(mark.snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                )
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
                //jeżeli nie uda się połączyć z bazą
            }
        }
        database.child("adventures").addListenerForSingleValueEvent(MarkerListener)
    }*/

    /*private fun initAdventures() {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adventureArray.clear()
                dataSnapshot.children.mapNotNullTo(adventureArray) { it.getValue<Adventure>(Adventure::class.java) }

                val advArray: MutableList<String> = mutableListOf()

                adventureArray.forEach{
                    advArray.add(it.name + "\n" + it.description + "\nLiczba punktów: " + it.markers!!.size.toString())
                }

                val adapter = ArrayAdapter(this@MapsActivity, android.R.layout.simple_list_item_1, advArray)
                adventurelist.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        database.child("adventures").addListenerForSingleValueEvent(menuListener)
    }*/
    private var adventureTemp: Adventure? = null
    private fun initAdventure() {
        //wczytanie markerów z bazy na mapę i dodanie tych markerów do tablicy
        val markerListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                    Toast.makeText(this@MapsActivity, "hi", Toast.LENGTH_SHORT).show()
                adventureArray.clear()
                dataSnapshot.children.mapNotNullTo(adventureArray) { it.getValue<Adventure>(Adventure::class.java) }
                //mapowanie z bazy do tablicy
                    Toast.makeText(this@MapsActivity, adventureArray.toString(), Toast.LENGTH_SHORT).show()
                adventureArray.forEach{
                    if(it.key=="-LXyY8X6ft6HS7IqVyhg"){
                        //jeżeli klucz przygody jest ten, którego poszukujemy, to stwórz przygodę z danych zawartych w bazie
                        adventureTemp = Adventure(it.name, it.description, it.markers!!, it.key)
                    }
                }

                /*adventureTemp!!.markers!!.forEach{
                    val mark = mMap!!.addMarker(MarkerOptions().title(it.title).snippet(it.snippet).position(LatLng(50.8,18.0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                    mark.tag = it.tag
                    //dodanie markerów na mapę

                    mMarkerArray.add(mark)
                }*/
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
                //jeżeli nie uda się połączyć z bazą
            }
        }
        database.child("adventures").addListenerForSingleValueEvent(markerListener)
    }

    private val markerClickListener = object : GoogleMap.OnMarkerClickListener {
        override fun onMarkerClick(marker: Marker?): Boolean {
            if (marker != yourmarker) {
                //jeśli znacznik nie jest znacznikiem pozycji użytkownika ma zrobić to co poniżej
                //pobranie wartości latitude i logitude pierwszego punktu trasy
                advlat=marker!!.position.latitude
                advlong=marker.position.longitude
                advname=marker.title        //tytuł przygody
                advsnippet = marker.snippet     //opis przygody
                //informacje o trasie dla użytkownika
                bStart.text="Rozpocznij przygodę " + advname
                bStart.visibility=Button.VISIBLE
                tvDesc.text="Opis przygody: " + advsnippet
                tvDesc.visibility=TextView.VISIBLE
                //Toast.makeText(this@MapsActivity, marker!!.position.latitude.toString(), Toast.LENGTH_SHORT).show()
                return true
            }

            // po kliknięciu w znacznik położenia użytkownika

            bStart.visibility=Button.INVISIBLE
            tvDesc.visibility=TextView.INVISIBLE
            return false
        }
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
        MarkerListener?.let {
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

        //po załadowaniu mapy nasłuchujemy czy znacznik został kliknięty
        with(mMap!!) {
            setOnMarkerClickListener(markerClickListener)
        }


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
                yourmarker = mMap!!.addMarker(MarkerOptions().position(user!!).title("Tu jesteś").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                if(bShowAdv.visibility==Button.GONE) {
                    // jeśli zmieni się położenie użytkownika a mają być pokazane trasy, to ma je pokazać
                    mMap!!.addMarker(
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
