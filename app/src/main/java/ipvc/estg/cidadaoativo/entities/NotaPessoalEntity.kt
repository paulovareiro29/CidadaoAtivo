package ipvc.estg.cidadaoativo.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notas_pessoais_table")
class NotaPessoalEntity(@PrimaryKey(autoGenerate = true) val id: Int? = null,
                        @ColumnInfo(name="titulo") val titulo: String,
                        @ColumnInfo(name="subtitulo") val subtitulo: String,
                        @ColumnInfo(name="descricao") val descricao: String)