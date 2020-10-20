package ipvc.estg.cidadaoativo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.cidadaoativo.adapter.NotaPessoalLineAdapter
import ipvc.estg.cidadaoativo.dataclasses.NotaPessoal
import kotlinx.android.synthetic.main.activity_notas_pessoais.*

class NotasPessoais : AppCompatActivity() {

    private lateinit var noteList: ArrayList<NotaPessoal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.notasPessoais) //mudar nome da action bar
        setContentView(R.layout.activity_notas_pessoais)

        noteList = ArrayList<NotaPessoal>()

        for(i in 0 until 20) {
            noteList.add(NotaPessoal("Titulo $i", "Subtitulo $i", "Uma grande descricao $i"))
        }

        lista_notas_pessoais.adapter = NotaPessoalLineAdapter(noteList)
        lista_notas_pessoais.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_notas_pessoais, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.criarNota -> {
                val intent = Intent(this, CriarNotaPessoal::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}