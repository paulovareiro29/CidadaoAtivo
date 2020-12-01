package ipvc.estg.cidadaoativo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.cidadaoativo.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private lateinit var editUsername: EditText
    private lateinit var editPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editUsername = findViewById(R.id.login_editUsername)
        editPassword = findViewById(R.id.login_editPassword)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_login_key), Context.MODE_PRIVATE
        )

        val user = sharedPref.getInt(getString(R.string.loginUsername), 0)

        if(user != 0){
            val intent = Intent(this,MapActivity::class.java)
            startActivity(intent)
        }
    }

    private fun hashString(type: String, input: String) =
        MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
            .map { String.format("%02X", it) }
            .joinToString(separator = "")

    fun login(view: View) {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.login(editUsername.text.toString(), hashString("SHA-1",editPassword.text.toString()))

        Toast.makeText(this@LoginActivity , hashString("SHA-1",editPassword.text.toString()), Toast.LENGTH_LONG).show()

        val intent = Intent(this,MapActivity::class.java)

        call.enqueue(object : Callback<UserPost> {
            override fun onResponse(call: Call<UserPost>, response: Response<UserPost>){
                if(response.isSuccessful){

                    val id = response.body()!!.id!!.toInt()

                    val sharedPref: SharedPreferences = getSharedPreferences(
                        getString(R.string.preference_login_key), Context.MODE_PRIVATE)
                    with (sharedPref.edit()){
                        putInt(getString(R.string.loginUsername), id)
                        commit()
                    }

                    startActivity(intent)
                }else{
                    editPassword.setError("")
                    editUsername.setError("")
                }
            }

            override fun onFailure(call: Call<UserPost>, t: Throwable){
                Toast.makeText(this@LoginActivity , "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_login, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_login_notasPessoais -> {
                val intent = Intent(this,NotasPessoais::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}