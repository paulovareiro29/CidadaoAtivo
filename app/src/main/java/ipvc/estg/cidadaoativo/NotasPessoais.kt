package ipvc.estg.cidadaoativo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.cidadaoativo.adapter.NotaPessoalLineAdapter
import ipvc.estg.cidadaoativo.adapter.OnNotaPessoalListener
import ipvc.estg.cidadaoativo.entities.NotaPessoalEntity
import ipvc.estg.cidadaoativo.viewModel.NotaPessoalViewModel
import kotlinx.android.synthetic.main.activity_notas_pessoais.*

class NotasPessoais : AppCompatActivity(),  OnNotaPessoalListener{

    private lateinit var notaPessoalViewModel: NotaPessoalViewModel
    private val newNotaActivityRequestCode = 1
    private val editNotaActivityRequestCode = 2

    companion object{ //variaveis estaticas
        lateinit var instance: NotasPessoais
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Companion.instance = this

        setTitle(R.string.notasPessoais) //mudar nome da action bar
        setContentView(R.layout.activity_notas_pessoais)

        val adapter = NotaPessoalLineAdapter(this)

        lista_notas_pessoais.adapter = adapter
        lista_notas_pessoais.layoutManager = LinearLayoutManager(this)

        notaPessoalViewModel = ViewModelProvider(this).get(NotaPessoalViewModel::class.java)
        notaPessoalViewModel.allNotasPessoais.observe(this, Observer { notas ->
            notas?.let {adapter.setNotas(it)}
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data == null) return;


        if(requestCode == newNotaActivityRequestCode && resultCode == Activity.RESULT_OK){
            val titulo = data.getStringExtra("titulo")
            val subtitulo = data.getStringExtra("subtitulo")
            val descricao = data.getStringExtra("descricao")
            notaPessoalViewModel.insert(NotaPessoalEntity(
                titulo = titulo,
                subtitulo = subtitulo,
                descricao = descricao))
        }

        if(requestCode == editNotaActivityRequestCode && resultCode == Activity.RESULT_OK){
            val id = data.getIntExtra("id", -1)
            val titulo = data.getStringExtra("titulo")
            val subtitulo = data.getStringExtra("subtitulo")
            val descricao = data.getStringExtra("descricao")
            notaPessoalViewModel.update(NotaPessoalEntity(id, titulo, subtitulo, descricao))
        }
    }

    fun onFabClick(view: View){
        val intent = Intent(this, CriarNotaPessoal::class.java)
        startActivityForResult(intent, newNotaActivityRequestCode)
    }


    override fun onNotaPessoalDeleteClick(nota: NotaPessoalEntity, position: Int) {
        notaPessoalViewModel.delete(nota)
    }

    override fun onnotaPessoalEditClick(nota: NotaPessoalEntity, position: Int) {
        val intent = Intent(this, CriarNotaPessoal::class.java)
        intent.putExtra("id", nota.id)
        intent.putExtra("titulo", nota.titulo)
        intent.putExtra("subtitulo", nota.subtitulo)
        intent.putExtra("descricao", nota.descricao)

        startActivityForResult(intent, editNotaActivityRequestCode)
    }
}