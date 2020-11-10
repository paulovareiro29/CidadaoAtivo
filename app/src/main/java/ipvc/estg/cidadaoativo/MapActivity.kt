package ipvc.estg.cidadaoativo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.cidadaoativo.api.EndPoints
import ipvc.estg.cidadaoativo.api.Location
import ipvc.estg.cidadaoativo.api.OutputPost
import ipvc.estg.cidadaoativo.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    val newLocationRequestCode = 1
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        refreshMap()
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
                        mMap.addMarker(MarkerOptions().position(loc).title("By: ${entry.user.username}"))
                    }
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable){
                Toast.makeText(this@MapActivity , "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data == null) return;


        if(requestCode == newLocationRequestCode && resultCode == Activity.RESULT_OK){
            val latitude = data.getDoubleExtra("latitude", 0.0)
            val longitude = data.getDoubleExtra("longitude", 0.0)
            val descricao = data.getStringExtra("descricao")

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.createLocation(latitude,longitude,descricao,"photo",1)

            call.enqueue(object : Callback<OutputPost> {
                override fun onResponse(call: Call<OutputPost>, response: Response<OutputPost>){
                    refreshMap()
                }

                override fun onFailure(call: Call<OutputPost>, t: Throwable){
                    Toast.makeText(this@MapActivity , "${t.message}", Toast.LENGTH_LONG).show()
                }
            })

        }

    }

    fun getSingle(){
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getLocationById(2) //no futuro é dinamico por clique

        call.enqueue(object : Callback<Location> {
            override fun onResponse(call: Call<Location>, response: Response<Location>){
                if(response.isSuccessful){ //working
                    val entry: Location = response.body()!!
                    //Toast.makeText(this@MapActivity, "${entry.id} - ${entry.user.username} / ${entry.longitude}" ,Toast.LENGTH_LONG).show()

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

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
    }
}