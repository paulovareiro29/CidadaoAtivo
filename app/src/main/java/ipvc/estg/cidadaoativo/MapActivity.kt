package ipvc.estg.cidadaoativo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class MapActivity : AppCompatActivity() {
    val newLocationRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
    }

    fun onFabClick(view: View) {
        val intent = Intent(this, AddLocationActivity::class.java)
        startActivityForResult(intent, newLocationRequestCode)
    }
}