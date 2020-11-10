package ipvc.estg.cidadaoativo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import ipvc.estg.cidadaoativo.api.EndPoints
import ipvc.estg.cidadaoativo.api.Location
import ipvc.estg.cidadaoativo.api.OutputPost
import ipvc.estg.cidadaoativo.api.ServiceBuilder
import ipvc.estg.cidadaoativo.entities.NotaPessoalEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity() {
    val newLocationRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        /*val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getLocations()

        call.enqueue(object : Callback<List<Location>>{
            override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>){
                if(response.isSuccessful){ //working
                    /*for(entry in response.body()!!){
                        Toast.makeText(this@MapActivity, "${entry.id} - ${entry.latitude} / ${entry.longitude}" ,Toast.LENGTH_LONG).show()
                    }*/
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable){
                Toast.makeText(this@MapActivity , "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })*/

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
                    if(response.isSuccessful){
                        Toast.makeText(this@MapActivity, "SUCESS", Toast.LENGTH_LONG).show()
                    }
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

        call.enqueue(object : Callback<Location>{
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
}