package ipvc.estg.cidadaoativo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.notas_pessoais_recyclerline.view.*
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.cidadaoativo.R
import ipvc.estg.cidadaoativo.dataclasses.NotaPessoal

class NotaPessoalLineAdapter(val list: ArrayList<NotaPessoal>): RecyclerView.Adapter<LineViewHolder>() {

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


}

class LineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val titulo = itemView.titulo
    val subtitulo = itemView.subtitulo
    val descricao = itemView.descricao
}