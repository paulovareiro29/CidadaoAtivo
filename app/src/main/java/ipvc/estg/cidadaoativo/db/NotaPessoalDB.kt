package ipvc.estg.cidadaoativo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ipvc.estg.cidadaoativo.dao.NotaPessoalDao
import ipvc.estg.cidadaoativo.entities.NotaPessoalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(NotaPessoalEntity::class), version = 1,exportSchema = false)
abstract class NotaPessoalDB : RoomDatabase() {

    abstract fun NotaPessoalDao() : NotaPessoalDao

    /*private class NotaPessoalDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var notaDao = database.NotaPessoalDao()

                    // Delete all content here.
                    notaDao.deleteAll()

                    // Add sample words.
                    var nota = NotaPessoalEntity(1,"Hello","Subtitulo","Descricao")
                    notaDao.insert(nota)

                }
            }
        }
    }*/

    companion object {
        @Volatile
        private var INSTANCE: NotaPessoalDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NotaPessoalDB {
            val tempInstance = INSTANCE
            if( tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotaPessoalDB::class.java,
                    "notas_pessoais_database"
                )
                        //callback para popular base de dados
                    //.addCallback(NotaPessoalDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}