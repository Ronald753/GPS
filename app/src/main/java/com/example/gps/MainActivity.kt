package com.example.gps

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gps.Constante.INTERVAL_TIME
import com.example.gps.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

//este esta aun nivel mucho mas global ejemplo Constante global
//const val....
class MainActivity : AppCompatActivity() {
    //OJITO: esta forma no es necesariamente obligatoria de usar para este datos, solo es referencial
    //companion object se usa para definir  constantes que seran globales que sus valores son accedidos por cualquier instancia
    companion object { //constancia del objeto
        val PERMISSION_GRANTED = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
    }

    private lateinit var binding: ActivityMainBinding

    //se usara la herramienta de sevicion de google de localizacion
    private lateinit var fusedLocation : FusedLocationProviderClient //la gestodara para solicitar una localizacion
    private val PERMISSION_ID = 42
    private var isGpsEnabled = false
    private var latitude:Double = 0.0
    private var longitude: Double = 0.0

    private var distance = 0.0
    private var velocity = 0.0
    private var contador = 0//artilusjio para solucionar
    var distanciatotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fabGPS.setOnClickListener{
            enableGPSservices()
        }
        binding.fabCoordenadas.setOnClickListener {
            manageLocation()
        }
        binding.button.setOnClickListener {
            startActivity(Intent(this,PersistenciaActivity::class.java))
        }
    }

    /**
     * Situacion: configurar la habilitacion de gps en el celular
     * permisos
     */
    private fun enableGPSservices() {
        if (!hasGPSEnabled()) { //si no esta activado hace lo siguiente
            //alret dialaog en prramacion funcional
            AlertDialog.Builder(this)
                //.setTitle("")//("hola")  primera forma para no hardcorea tanto texto
                .setTitle(R.string.dialog_text_title)
                .setMessage(R.string.dialog_text_description)
                .setPositiveButton(R.string.dialog_button_accept,  //funcion lambda
                    DialogInterface.OnClickListener { dialog, wich ->
                        goToEnableGPS()
                    })//se llamara a un a intrrfaz anonima
                .setNegativeButton(R.string.dialog_button_deny) { dialog, wich ->
                    isGpsEnabled = false
                } //si ES el ultima parametro que entra en un una funcion, se hace lo siguiente, solo en este caso
                .setCancelable(true)
                .show()
        }else
            Toast.makeText(this, "El GSP ya esta activado", Toast.LENGTH_SHORT).show()
    }
    private fun goToEnableGPS() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun hasGPSEnabled():Boolean {
        //Manager: es el que lleva la batuta o es el que orgranizagestiona
        // lo referido al manejo de ciertos sevicios o recurso
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager //esta es la libreria para la localizacion
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    /**
     * Situacuin y solicitud de permisos en la app para poder usar GPS
    * */
    //metodo checkselfPermission: evalue el calor que tiene en tu app cierto oermiso , no verifica si tienens o no el permiso
    //solo ve que valor numerico tiene ese calor asignado ese permiso en tu appp
    //PERMISIONGRANTED es un valor numerico general en android que representa el valor que significa un permiso  otorado
    //resica si andrpod
    private fun allPermissionsGrnated():Boolean =
        PERMISSION_GRANTED.all{
            ActivityCompat.
            checkSelfPermission(baseContext, it) == //por que el contexto por que no puede ser this, pues por que stamos dentro de un mabito anonimo o flecha
                    PackageManager.PERMISSION_GRANTED
        }
    /*
    private fun checkPermission():Boolean{
        return ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    */

    private fun requestPermissionUser(){
        //Lanzar la ventana al usuario para solicitarle que habilite el permisos o los deniegue

        //to_do lo que tiene que ver con revisar permisso se enuentra aca ActivitiCompact
        ActivityCompat.requestPermissions(
            this,
            PERMISSION_GRANTED, //no se pone una lista por que como requerimiento dice que necesita u array y no una lista
            PERMISSION_ID //42 por que es el codigo de acceso
        )
    }
    /**
     * Situacion: obtencion de coordenadas
     * sonfiguracion deonjeto
     * obtiene localizacion llamado FusedLocation
     *
     * callBack llamada a una solicitud
     * */

    @SuppressLint("MissingPermission")
    private fun manageLocation(){
        if (hasGPSEnabled()){
            if (allPermissionsGrnated()){
                fusedLocation = LocationServices.getFusedLocationProviderClient(this,)
                fusedLocation.lastLocation.addOnSuccessListener { location -> getCoordinates() }
            }
            else
            {
                requestPermissionUser()
            }
        }else{ goToEnableGPS() }
    }

    @SuppressLint("MissingPermission")
    private fun getCoordinates() {
        //para versiones de Google gms location 21 y superior
        val locationrequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            INTERVAL_TIME
        ).setMaxUpdates(300) //rango de cien veces no de coordenadas
            //.setMinUpdateDistanceMeters() //com var a setear cuda cuanto tipo saltara la hubucacion exacta
            .build()
        //para versiones de google gms location 20 e inferiores
        /*val locationrequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 0  //si esatas pidioen do en segundos
            fastestInterval = 0 // se configura dependiendo edel interval
            numUpdates = 1
        }*/
        //siempre te molestara por una funcion que necesita permisos
        fusedLocation.requestLocationUpdates( //para solciona en esta parte alt+ enter -> segunda opcion
            locationrequest,
            mylocationcallback,
            Looper.myLooper()
        )
    }
    //en esta parte se obtendra las tan deseadas latitud y longitud
    private val mylocationcallback = object : LocationCallback(){
        override fun onLocationResult(locationresult: LocationResult) {
            val mylastlocation : Location? = locationresult.lastLocation
            if (mylastlocation != null){
                //los nuevos datos
                var lastlatitude = mylastlocation.latitude
                val lastlongitude = mylastlocation.latitude
                if (contador > 0){
                    distance = calcularDistance(lastlatitude,lastlongitude)
                    velocity = calculateVelocity()
                    distanciatotal += distance
                }
                binding.apply {
                    txtLatitud.text = lastlatitude.toString()
                    ttlogitud.text = lastlongitude.toString()
                    txtDistancia.text = "$distance mts."
                    txtVelocidad.text = "$velocity km/h"
                    txtTotalca.text = "$distanciatotal"
                }
                //los valores precios
                latitude = lastlongitude // mylastlocation.latitude
                longitude = lastlongitude //mylastlocation.longitude
                contador ++
                getAddressInfo()
            }
        }
    }

    private fun getAddressInfo() {
        //la clase para obtener direcciones a partide coordenadases se llama GEOcoder
        //Puden obtener de 1 a n direcciones Siempre en formato de array
        //en caso de tener intersecciones se muestra la la mas imporatante
        val geocoder = Geocoder(this)
        try {
            var addresses = geocoder.getFromLocation(latitude, longitude,1)
            binding.txtDireccion.text = addresses.get(0).getAddressLine(0)
        }catch (e: Exception){
            binding.txtDireccion.text = "No se puede obtener direccion"
        }
    }

    private fun calcularDistance(lastLatitude:Double, lastLongitude :Double ):Double{
        val earthRadius = 6371.0 //kilometros
        //para allar el angulo se hace con radianes
        val difLatitude = Math.toRadians(lastLatitude - latitude)
        val difLongitude = Math.toRadians(lastLongitude - latitude)
        val sinLatitude = sin(difLatitude / 2)
        val sinLongitude = sin(difLongitude / 2)
        val result = Math.pow(sinLatitude,2.0) +
                (Math.pow(sinLongitude,2.0)
                        * cos(Math.toRadians(latitude))
                        * cos(Math.toRadians(lastLongitude)))
        val resul2 = 2 * atan2(sqrt(result), sqrt(1 - result))
        val distance = (earthRadius * resul2) * 1000.0
        return distance
    }
    //condiciones ideales
    private fun calculateVelocity():Double = (distance / INTERVAL_TIME) * 3.6 //para ver el tiempo km/h
}








