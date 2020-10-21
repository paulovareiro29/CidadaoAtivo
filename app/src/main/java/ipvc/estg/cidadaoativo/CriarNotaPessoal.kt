package ipvc.estg.cidadaoativo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText

class CriarNotaPessoal : AppCompatActivity() {

    private lateinit var editTitulo: EditText
    private lateinit var editSubtitulo: EditText
    private lateinit var editDescricao: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_nota_pessoal)
        editTitulo = findViewById(R.id.editTitulo)
        editSubtitulo = findViewById(R.id.editSubtitulo)
        editDescricao = findViewById(R.id.editDescricao)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_criar_notas_pessoais,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.guardarNota -> {
                val replyIntent = Intent()
                if(TextUtils.isEmpty(editTitulo.text) || TextUtils.isEmpty(editSubtitulo.text) || TextUtils.isEmpty(editDescricao.text)){
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                }else{
                    val titulo = editTitulo.text.toString()
                    val subtitulo = editSubtitulo.text.toString()
                    val descricao = editDescricao.text.toString()

                    replyIntent.putExtra("titulo", titulo)
                    replyIntent.putExtra("subtitulo", subtitulo)
                    replyIntent.putExtra("descricao", descricao)
                    setResult(Activity.RESULT_OK, replyIntent)
                }
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}