package ipvc.estg.cidadaoativo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ipvc.estg.cidadaoativo.db.NotaPessoalDB
import ipvc.estg.cidadaoativo.entities.NotaPessoalEntity
import ipvc.estg.cidadaoativo.repository.NotaPessoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotaPessoalViewModel(application: Application): AndroidViewModel(application) {
    private val repository: NotaPessoalRepository

    val allNotasPessoais: LiveData<List<NotaPessoalEntity>>

    init {
        val notasDao = NotaPessoalDB.getDatabase(application, viewModelScope).NotaPessoalDao()
        repository = NotaPessoalRepository(notasDao)
        allNotasPessoais = repository.allNotas
    }

    fun insert(notaPessoalEntity: NotaPessoalEntity) = viewModelScope.launch(Dispatchers.IO)  {
        repository.insert(notaPessoalEntity)
    }
}