package br.edu.scl.ifsp.sdm.intents

import br.edu.scl.ifsp.sdm.intents.Constants.PARAMETRO_EXTRA
import br.edu.scl.ifsp.sdm.intents.databinding.ActivityParameterBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ParameterActivity : AppCompatActivity() {
    private val apb: ActivityParameterBinding by lazy{
        ActivityParameterBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(apb.root)

        intent.getStringExtra(PARAMETRO_EXTRA)?.let{
            apb.parameterEt.setText(it)
        }

        apb.returnCloseBt.setOnClickListener {
            val parameter = apb.parameterEt.text.toString()
            val intentResult = Intent()
            intentResult.putExtra(PARAMETRO_EXTRA, parameter)
            setResult(RESULT_OK, intentResult)
            finish()
        }
    }
}