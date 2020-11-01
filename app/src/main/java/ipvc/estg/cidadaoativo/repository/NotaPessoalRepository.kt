package ipvc.estg.cidadaoativo.repository

import androidx.lifecycle.LiveData
import ipvc.estg.cidadaoativo.dao.NotaPessoalDao
import ipvc.estg.cidadaoativo.entities.NotaPessoalEntity


class NotaPessoalRepository(private val notaPessoalDao: NotaPessoalDao) {

    val allNotas: LiveData<List<NotaPessoalEntity>> = notaPessoalDao.getTodasNotasPessoais()

    suspend fun insert(notaPessoalEntity: NotaPessoalEntity){
        notaPessoalDao.insert(notaPessoalEntity)
    }

    suspend fun delete(notaPessoalEntity: NotaPessoalEntity){
        notaPessoalDao.delete(notaPessoalEntity)
    }

    suspend fun update(notaPessoalEntity: NotaPessoalEntity){
        notaPessoalDao.update(notaPessoalEntity)
    }

}