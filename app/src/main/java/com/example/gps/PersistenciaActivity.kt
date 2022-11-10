package com.example.gps

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.gps.Constante.FILE_NAME
import com.example.gps.Constante.KEY_VALORATION
import com.example.gps.Constante.KEY_VALUE
import com.example.gps.databinding.ActivityPersistenciaBinding

class PersistenciaActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPersistenciaBinding

    //variables que usaremos
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersistenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialSharedPreference()
        loadData()
        binding.btnMostrar.setOnClickListener {
            saveData()
            loadData()
        }
    }
    //configurar el archivo persistente
    private fun initialSharedPreference() {
        //priemro busca en el disposutiv un archivo con ese nombre
        //si no existe ek archivo lo va a crear
        //pero si existe va a traer el archivo
        //asignando su archivo a una variable para manejar el archivo por codigo
                                                    //con estaclave solo la app podra abrir este archivo
        sharedPreference = getSharedPreferences(FILE_NAME, MODE_PRIVATE) //-> "Nombre ",valor entero como clave para ingresar al archivo de tipo entero
        //considerar tener un variable donde gestiona el archivo en modo de escritura
        editor = sharedPreference.edit() //editor es el archivo en modo escritura
    }
    //guradar datos en el archivo
    private fun saveData(){
        //en los shared preferences la informacion se guarda en fromato de registro
        //cada registro se guarda en formato LLAVE - VALOR
        val myhobby = binding.etHobbie.text.toString()
        //cuadno guarda barre el archivo y busta si existe ya esa llave
        //si ya existe en ese registro va a remamplaxzar el valor
        //si no existe recien va a crear el registro
        editor.apply{
            putString(KEY_VALUE,myhobby)
            putInt(KEY_VALORATION,100)
        }.apply()
        //1. usar APPLY(): es un guardado asincrono
        //2. usar commit(): en un guardado sincrono
    }
    private fun loadData(){
        val myhobby = sharedPreference.getString(KEY_VALUE, "vacio")
        val valor = sharedPreference.getInt(KEY_VALORATION,0)
        binding.txtResultado.text = "Mis datos son $myhobby, $valor..."
    }
}