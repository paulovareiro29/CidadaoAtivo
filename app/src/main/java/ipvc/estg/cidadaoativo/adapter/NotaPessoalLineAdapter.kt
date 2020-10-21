package ipvc.estg.cidadaoativo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.notas_pessoais_recyclerline.view.*
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.cidadaoativo.R
import ipvc.estg.cidadaoativo.entities.NotaPessoalEntity

class NotaPessoalLineAdapter(): RecyclerView.Adapter<LineViewHolder>() {


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

        holder.titulo.text = currentNota.titulo
        holder.subtitulo.text = currentNota.subtitulo
        holder.descricao.text = currentNota.descricao
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
}