package ipvc.estg.cidadaoativo

import android.app.Activity
import android.content.Intent
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
import ipvc.estg.cidadaoativo.api.EndPoints
import ipvc.estg.cidadaoativo.api.Location
import ipvc.estg.cidadaoativo.api.OutputPost
import ipvc.estg.cidadaoativo.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLocationActivity : AppCompatActivity() {

    private lateinit var editLatitude: EditText
    private lateinit var editLongitude: EditText
    private lateinit var editDescricao: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)

        editLatitude = findViewById(R.id.addLocation_latitude)
        editLongitude = findViewById(R.id.addLocation_longitude)
        editDescricao = findViewById(R.id.addLocation_descricao)
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
                    val longitude = editLatitude.text.toString().toDouble()
                    val latitude = editLongitude.text.toString().toDouble()
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