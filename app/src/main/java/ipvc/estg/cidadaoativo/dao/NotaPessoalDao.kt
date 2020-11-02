package ipvc.estg.cidadaoativo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ipvc.estg.cidadaoativo.entities.NotaPessoalEntity

@Dao
interface NotaPessoalDao {

    @Query("SELECT * FROM notas_pessoais_table ORDER BY id DESC")
    fun getTodasNotasPessoais(): LiveData<List<NotaPessoalEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notaPessoal: NotaPessoalEntity)

    @Query("DELETE FROM notas_pessoais_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(notaPessoal: NotaPessoalEntity)

    @Update
    suspend fun update(notaPessoal: NotaPessoalEntity)

}