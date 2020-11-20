package ipvc.estg.cidadaoativo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class AddLocationActivity : AppCompatActivity() {

    private lateinit var editLatitude: TextView
    private lateinit var editLongitude: TextView
    private lateinit var editDescricao: EditText

    private lateinit var lastLocation: android.location.Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)

        editLatitude = findViewById(R.id.addLocation_latitude)
        editLongitude = findViewById(R.id.addLocation_longitude)
        editDescricao = findViewById(R.id.addLocation_descricao)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                var loc = LatLng(lastLocation.latitude, lastLocation.longitude)

                editLatitude.setText(loc.latitude.toString())
                editLongitude.setText(loc.longitude.toString())
            }
        }

        createLocationRequest()

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 10
            )
            finish()
            return
        }else{
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }

    }

    fun createLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest.numUpdates = 1
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


    }

    fun upload(view: View) {}



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_addlocation,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_addlocation_guardar -> {
                val replyIntent = Intent()
                if(TextUtils.isEmpty(editLatitude.text) || TextUtils.isEmpty(editLongitude.text) || TextUtils.isEmpty(editDescricao.text)){
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                    Toast.makeText(this,R.string.validacaoNotaPessoal,Toast.LENGTH_LONG).show()
                }else{
                    val latitude = editLatitude.text.toString().toDouble()
                    val longitude = editLongitude.text.toString().toDouble()
                    val descricao = editDescricao.text.toString()

                    replyIntent.putExtra("longitude", longitude)
                    replyIntent.putExtra("latitude", latitude)
                    replyIntent.putExtra("descricao", descricao)
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()

                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}