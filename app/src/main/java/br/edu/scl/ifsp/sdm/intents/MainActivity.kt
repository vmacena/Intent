package br.edu.scl.ifsp.sdm.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_INTENT
import android.content.Intent.EXTRA_TITLE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.scl.ifsp.sdm.intents.Constants.PARAMETRO_EXTRA
import br.edu.scl.ifsp.sdm.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var parl: ActivityResultLauncher<Intent>
    private lateinit var callPermissionArl: ActivityResultLauncher<String>
    private lateinit var pickImageArl : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        amb.parameterBt.setOnClickListener {
            Intent(this, ParameterActivity::class.java).also {
                it.putExtra(PARAMETRO_EXTRA, amb.parameterTv.text.toString())
                parl.launch(it)
            }
        }

        parl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getStringExtra(PARAMETRO_EXTRA)?.let {
                    amb.parameterTv.text = it
                }
            }
        }
        callPermissionArl = registerForActivityResult(ActivityResultContracts.RequestPermission()){
                permissionAccepted ->
            if(permissionAccepted){
                callNumber(call = true)
            }else{
                Toast.makeText(this,"Permissao necessaria para continuar", Toast.LENGTH_LONG).show()
            }
        }

        pickImageArl= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if(result.resultCode == RESULT_OK){
                result.data?.data?.let { imageUri ->amb.parameterTv.text = imageUri.toString()
                    Intent(ACTION_VIEW, imageUri).also { startActivity(it) }}
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.openActivityMi -> {
                true
            }
            R.id.viewMi -> {
                val url: Uri = Uri.parse(amb.parameterTv.text.toString())
                val browserIntent = Intent(ACTION_VIEW, url)
                startActivity(browserIntent)
                true
            }

            R.id.callMi -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        callNumber(call = true)
                    } else {
                        callPermissionArl.launch(CALL_PHONE)
                    }
                }
                else{
                    callNumber(call = true)
                }
                true
            }

            R.id.dialMi -> {
                callNumber(call = false)
                true
            }

            R.id.pickMi -> {
                val pickImageIntent = Intent(ACTION_PICK)
                val imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                pickImageIntent.setDataAndType(Uri.parse(imagePath),"image/*")
                pickImageArl.launch(pickImageIntent)
                true
            }

            R.id.chooserMi -> {
                Uri.parse(amb.parameterTv.text.toString()).let{uri ->
                    Intent(ACTION_VIEW, uri).also {browserIntent ->
                        val chooseAppIntent = Intent(ACTION_CHOOSER)
                        chooseAppIntent.putExtra(EXTRA_TITLE, "Escolha seu navegador favorito")
                        chooseAppIntent.putExtra(EXTRA_INTENT, browserIntent)
                        startActivity(chooseAppIntent)
                    }
                }
                true
            }

            else -> {
                false
            }
        }
    }

    private fun callNumber(call: Boolean){
        val numberUri:Uri = Uri.parse("tel:${amb.parameterTv.text}")
        val callIntent = Intent(if(call) ACTION_CALL else ACTION_DIAL)
        callIntent.data = numberUri
        startActivity(callIntent)
    }
}