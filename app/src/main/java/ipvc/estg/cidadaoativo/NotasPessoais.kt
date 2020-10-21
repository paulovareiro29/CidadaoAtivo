package ipvc.estg.cidadaoativo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.cidadaoativo.adapter.NotaPessoalLineAdapter
import ipvc.estg.cidadaoativo.entities.NotaPessoalEntity
import ipvc.estg.cidadaoativo.viewModel.NotaPessoalViewModel
import kotlinx.android.synthetic.main.activity_notas_pessoais.*

class NotasPessoais : AppCompatActivity() {

    private lateinit var notaPessoalViewModel: NotaPessoalViewModel
    private val newNotaActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.notasPessoais) //mudar nome da action bar
        setContentView(R.layout.activity_notas_pessoais)

        val adapter = NotaPessoalLineAdapter()

        lista_notas_pessoais.adapter = adapter
        lista_notas_pessoais.layoutManager = LinearLayoutManager(this)

        notaPessoalViewModel = ViewModelProvider(this).get(NotaPessoalViewModel::class.java)
        notaPessoalViewModel.allNotasPessoais.observe(this, Observer { notas ->
            notas?.let {adapter.setNotas(it)}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data?.equals(null)!!){
            return;
        }

        if(requestCode == newNotaActivityRequestCode && resultCode == Activity.RESULT_OK){
            val titulo = data.getStringExtra("titulo")
            val subtitulo = data.getStringExtra("subtitulo")
            val descricao = data.getStringExtra("descricao")
            notaPessoalViewModel.insert(NotaPessoalEntity(titulo = titulo,subtitulo = subtitulo,descricao = descricao))
        }
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
                startActivityForResult(intent, newNotaActivityRequestCode)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}