package ipvc.estg.cidadaoativo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.cidadaoativo.NotasPessoais
import ipvc.estg.cidadaoativo.R
import ipvc.estg.cidadaoativo.entities.NotaPessoalEntity
import ipvc.estg.cidadaoativo.viewModel.NotaPessoalViewModel
import kotlinx.android.synthetic.main.notas_pessoais_recyclerline.view.*

class NotaPessoalLineAdapter(var clickListener: OnNotaPessoalListener): RecyclerView.Adapter<LineViewHolder>(){


    private var list = emptyList<NotaPessoalEntity>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notas_pessoais_recyclerline, parent, false)
        return LineViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val currentNota = list[position]

        holder.init(currentNota, clickListener)
    }


    internal fun setNotas(notasPessoais: List<NotaPessoalEntity>){
        this.list = notasPessoais
        notifyDataSetChanged()
    }

}

class LineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val titulo = itemView.titulo
    val subtitulo = itemView.subtitulo
    val descricao = itemView.descricao
    val deleteBtn = itemView.deleteButton
    val editBtn = itemView.editButton

    fun init(item: NotaPessoalEntity, action: OnNotaPessoalListener) {
        titulo.text = item.titulo
        subtitulo.text = item.subtitulo
        descricao.text = item.descricao

        deleteBtn.setOnClickListener {
            action.onNotaPessoalDeleteClick(
                item, adapterPosition
            )
        }

        editBtn.setOnClickListener {
            action.onnotaPessoalEditClick(
                item, adapterPosition
            )
        }
    }
}

interface OnNotaPessoalListener {
    fun onNotaPessoalDeleteClick(nota: NotaPessoalEntity, position: Int)
    fun onnotaPessoalEditClick(nota: NotaPessoalEntity, position: Int)
}
