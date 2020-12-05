package ipvc.estg.cidadaoativo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.cidadaoativo.api.EndPoints
import ipvc.estg.cidadaoativo.api.Location
import ipvc.estg.cidadaoativo.api.LocationPost
import ipvc.estg.cidadaoativo.api.ServiceBuilder
import ipvc.estg.cidadaoativo.geofence.GeofenceHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    val newLocationRequestCode = 1

    private lateinit var mMap: GoogleMap

    private var user_id: Int = 0

    private lateinit var settingsPreferences: SharedPreferences

    private lateinit var lastLocation: android.location.Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper

    private val GEOFENCE_RADIUS: Float = 100.0F

    private val geofenceList = mutableListOf<String>()

    private lateinit var sensorManager: SensorManager

    private lateinit var sensorAccelerometer: Sensor
    private lateinit var sensorMagneticField: Sensor
    private lateinit var sensorTemperature: Sensor

    private var mGravity = FloatArray(3)
    private var mGeoMagnetic = FloatArray(3)

    private var azimuth = 0f
    private var currectAzimuth = 0f

    private lateinit var pointer: ImageView
    private lateinit var temperatureTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


         settingsPreferences = getSharedPreferences(
            getString(R.string.preference_settings_key), Context.MODE_PRIVATE
        )

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_login_key), Context.MODE_PRIVATE
        )

        user_id = sharedPref.getInt(getString(R.string.loginUsername), 0)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                refreshMap()
            }
        }
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)

        createLocationRequest()

        //sensores
        pointer = findViewById<ImageView>(R.id.pointer)
        temperatureTextView = findViewById<TextView>(R.id.map_temperatura)

        sensorManager= getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        val temperatureListener = object: SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor, p1: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {
                temperatureTextView.text = "${event.values[0]}ºC"
            }

        }

        val accelerometerListener = object: SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor, p1: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {
                var alpha = 0.97f
                mGravity[0] = alpha*mGravity[0]+(1-alpha)*event.values[0]
                mGravity[1] = alpha*mGravity[1]+(1-alpha)*event.values[1]
                mGravity[2] = alpha*mGravity[2]+(1-alpha)*event.values[2]

                UpdateBussula()
            }

        }

        val magneticFieldListener = object: SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor, p1: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {
                var alpha = 0.97f
                mGeoMagnetic[0] = alpha*mGeoMagnetic[0]+(1-alpha)*event.values[0]
                mGeoMagnetic[1] = alpha*mGeoMagnetic[1]+(1-alpha)*event.values[1]
                mGeoMagnetic[2] = alpha*mGeoMagnetic[2]+(1-alpha)*event.values[2]

                UpdateBussula()
            }

        }

        sensorManager.registerListener(accelerometerListener, sensorAccelerometer ,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(magneticFieldListener, sensorMagneticField ,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(temperatureListener, sensorTemperature ,SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun UpdateBussula () {
        var R = FloatArray(9)
        var I = FloatArray(9)
        var success: Boolean = SensorManager.getRotationMatrix(R,I,mGravity,mGeoMagnetic)
        if(success) {


            var orientation = FloatArray(3)
            SensorManager.getOrientation(R,orientation)
            azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            azimuth = (azimuth+360)%360

            var anim: Animation = RotateAnimation(-currectAzimuth,-azimuth,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
            currectAzimuth = azimuth

            anim.duration = 100
            anim.repeatCount = 0
            anim.fillAfter = true

            Log.d("TAAAG", "${azimuth}")
            pointer.startAnimation(anim)
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onBackPressed() {

    }

    fun refreshMap(){
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getLocations()

        call.enqueue(object : Callback<List<Location>>{
            override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>){
                if(response.isSuccessful){ //working
                    mMap.clear()
                    for(entry in response.body()!!){
                        val loc = LatLng(entry.latitude, entry.longitude)
                        if(calculateDistance(entry.latitude,entry.longitude,lastLocation.latitude,lastLocation.longitude) < 1000){
                            mMap.addMarker(MarkerOptions().position(loc).title("${entry.id}"))
                            addCircle(loc, GEOFENCE_RADIUS)

                            if(!geofenceAlreadyExists("${entry.id}")) {
                                geofenceList.add("${entry.id}")
                                addGeofence("${entry.id}", loc, GEOFENCE_RADIUS)
                            }
                        }


                    }
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable){
                Toast.makeText(this@MapActivity , "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun geofenceAlreadyExists(id: String) : Boolean {
        for(geo in geofenceList){
            if(geo == id){
                return true
            }
        }
        return false
    }

    fun addGeofence(id: String, latlng: LatLng, radius: Float){
        val geofence = geofenceHelper.getGeofence(id, latlng,radius, Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.getPendingIntent()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener{
                Log.d("MapActivity", "onSuccess: Geofence Added...")
            }
            .addOnFailureListener {
                val errorMessage = geofenceHelper.getErrorString(it)
                Log.d("MapActivity", "onFailure: " + errorMessage)
            }
    }

    fun addCircle(latlng: LatLng, radius: Float){
        val circleOptions = CircleOptions()
        circleOptions.center(latlng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255,255,0,0))
        circleOptions.fillColor(Color.argb(64,255,0,0))
        circleOptions.strokeWidth(4F)
        mMap.addCircle(circleOptions)
    }

    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float{
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lng1, lat2, lng2, results)

        return results[0]
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data == null) return;


        if(requestCode == newLocationRequestCode && resultCode == Activity.RESULT_OK){
            val latitude = data.getDoubleExtra("latitude", 0.0)
            val longitude = data.getDoubleExtra("longitude", 0.0)
            val descricao = data.getStringExtra("descricao")

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.createLocation(latitude,longitude,descricao,"photo",user_id)

            call.enqueue(object : Callback<LocationPost> {
                override fun onResponse(call: Call<LocationPost>, response: Response<LocationPost>){
                    refreshMap()
                }

                override fun onFailure(call: Call<LocationPost>, t: Throwable){
                    Toast.makeText(this@MapActivity , "${t.message}", Toast.LENGTH_LONG).show()
                }
            })

        }

    }

    fun popUpEditMarker(entry: Location){
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.edit2_marker_popup, null)

        // create the popup window
        val width: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable =
            true // lets taps outside the popup also dismiss it

        val popupWindow = PopupWindow(popupView, width, height, focusable)

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


        popupView.findViewById<TextView>(R.id.edit_marker_criadoPor_text).setText(entry.user.username)
        popupView.findViewById<EditText>(R.id.edit_marker_descricao_text).setText(entry.descricao)


        popupView.findViewById<ImageButton>(R.id.edit_marker_saveButton).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                save(   entry.id,
                        entry.latitude,
                        entry.longitude,
                        popupView.findViewById<EditText>(R.id.edit_marker_descricao_text).text.toString(),
                        entry.user.id)

                popupWindow.dismiss()
            }
        })

    }

    fun popUpSameUserMarker(entry: Location){
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.edit_marker_popup, null)

        // create the popup window
        val width: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable =
            true // lets taps outside the popup also dismiss it

        val popupWindow = PopupWindow(popupView, width, height, focusable)

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


        popupView.findViewById<TextView>(R.id.edit_marker_criadoPor_text).setText(entry.user.username)
        popupView.findViewById<TextView>(R.id.edit_marker_descricao_text).setText(entry.descricao)

        popupView.findViewById<ImageButton>(R.id.edit_marker_deleteButton).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                delete(entry.id)
                popupWindow.dismiss()
            }
        })

        popupView.findViewById<ImageButton>(R.id.edit_marker_editButton).setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                popupWindow.dismiss()
                popUpEditMarker(entry)
            }
        })
    }

    fun popUpMarker(entry: Location){
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.marker_popup, null)

        // create the popup window
        val width: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable =
            true // lets taps outside the popup also dismiss it

        val popupWindow = PopupWindow(popupView, width, height, focusable)

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)


        popupView.findViewById<TextView>(R.id.edit_marker_criadoPor_text).setText(entry.user.username)
        popupView.findViewById<TextView>(R.id.edit_marker_descricao_text).setText(entry.descricao)




    }

    fun save(id: Int, latitude: Double, longitude: Double, descricao: String, userId: Int){
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.updateLocation(id, latitude, longitude, descricao, "", userId)

        call.enqueue(object : Callback<LocationPost> {
            override fun onFailure(call: Call<LocationPost>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<LocationPost>, response: Response<LocationPost>) {
                TODO("Not yet implemented")
            }

        })
    }

    fun delete(id: Int){
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.delete(id)

        call.enqueue(object : Callback<Location> {
            override fun onFailure(call: Call<Location>, t: Throwable) {

            }

            override fun onResponse(call: Call<Location>, response: Response<Location>) {

            }

        })
    }

    fun getSingle(id: Int){
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getLocationById(id)

        call.enqueue(object : Callback<Location> {
            override fun onResponse(call: Call<Location>, response: Response<Location>){
                if(response.isSuccessful){ //working
                    val entry: Location = response.body()!!

                    if(user_id == entry.user.id){
                        popUpSameUserMarker(entry)
                    }else{
                        popUpMarker(entry)
                    }

                }
            }

            override fun onFailure(call: Call<Location>, t: Throwable){
                Toast.makeText(this@MapActivity , "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun onFabClick(view: View) {
        val intent = Intent(this, AddLocationActivity::class.java)
        startActivityForResult(intent, newLocationRequestCode)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.setOnMarkerClickListener(this)

        setUpMap()
    }

    fun setUpMap(){
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 10)

            return
        }

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), 20
            )

            return
        }

        
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this){ location ->
                if(location != null) {
                    lastLocation = location

                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                    startLocationUpdates()
                }
            }


    }

    fun startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        getSingle(marker.title.toInt())
        return true
    }

    fun createLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }






    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_map, menu)
        return true;
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val activated = settingsPreferences.getBoolean("notifications", false)

        if(activated) {
            menu.findItem(R.id.menu_map_notification).setTitle(R.string.desativar_notificacoes)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_map_logout -> {
                val sharedPref: SharedPreferences = getSharedPreferences(
                    getString(R.string.preference_login_key), Context.MODE_PRIVATE)
                with (sharedPref.edit()){
                    putInt(getString(R.string.loginUsername), 0)
                    commit()
                }

                finish()
                true
            }
            R.id.menu_map_notification -> {
                val activated = settingsPreferences.getBoolean("notifications",false)
                if(activated){
                    settingsPreferences
                    with (settingsPreferences.edit()){
                        putBoolean("notifications", false)
                        commit()
                    }
                    Toast.makeText(this,"Notificações desativadas", Toast.LENGTH_SHORT).show()
                    item.setTitle(R.string.ativar_notificacoes)

                }else{
                    settingsPreferences
                    with (settingsPreferences.edit()){
                        putBoolean("notifications", true)
                        commit()
                    }
                    Toast.makeText(this,"Notificações ativadas", Toast.LENGTH_SHORT).show()
                    item.setTitle(R.string.desativar_notificacoes)
                }

                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
}