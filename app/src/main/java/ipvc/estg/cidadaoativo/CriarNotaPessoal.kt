package ipvc.estg.cidadaoativo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CriarNotaPessoal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.criarNota)
        setContentView(R.layout.activity_criar_nota_pessoal)
    }


}