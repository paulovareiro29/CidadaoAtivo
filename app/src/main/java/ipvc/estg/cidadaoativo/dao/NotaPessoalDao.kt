package ipvc.estg.cidadaoativo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ipvc.estg.cidadaoativo.entities.NotaPessoalEntity

@Dao
interface NotaPessoalDao {

    @Query("SELECT * FROM notas_pessoais_table ORDER BY id")
    fun getTodasNotasPessoais(): LiveData<List<NotaPessoalEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notaPessoal: NotaPessoalEntity)

    @Query("DELETE FROM notas_pessoais_table")
    suspend fun deleteAll()

    //acrescentar um de delete e update
}