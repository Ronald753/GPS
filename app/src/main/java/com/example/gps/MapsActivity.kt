package com.example.gps

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope

import com.example.gps.Coordenadas.cementerioJudios
import com.example.gps.Coordenadas.lapaz
import com.example.gps.Coordenadas.plazaAbaroa
import com.example.gps.Coordenadas.univalle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.gps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//los mapas se cargan asincronamente
            //una funcion que hace un apeticion de respuesta //cuando el mapa esta listo que respondemos
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var contadorMarcador: Int = 0
    private var isGpsEnabled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //enableGPSservices()
        Utils.binding=binding

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        //El mapa se carga asincronamente
        //No satura un proceso principal  de la UI
        mapFragment.getMapAsync(this)
        //Activar evento listener de conjunto de botones
        setupToggleButtons()

    }
    /*private fun hasGPSEnabled():Boolean {
        //Manager: es el que lleva la batuta o es el que orgranizagestiona
        // lo referido al manejo de ciertos sevicios o recurso
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager //esta es la libreria para la localizacion
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun goToEnableGPS() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
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
    }*/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //Es una funcion de respuesta cuando el mapa esta listo para trabajar
    //el parametro que tienen devuelve el mapa de
    //Google listo y configurado
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        //Delimitar el rango de Zoom de la cámara
        //para evitar que el usuario haga un
        //zoom in o out de la camara
        mMap.apply {
            setMinZoomPreference(15f)
            setMaxZoomPreference(19f)
        }

        // se define coordenadas en un objeto
        //LatLng que conjuncio latitud y longitud
        val campNou = LatLng(41.3810569827446, 2.1227983405287594)

        //Marcadores .... Tachuelas rojas
        mMap.addMarker(MarkerOptions()
            .position(campNou)
            .title("Mi lugar favorito")
            .snippet("${campNou.latitude},${campNou.longitude}"))

        //Pocicionar la camara en la ubicacion de preferencia
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campNou, 15f))

        /**
         * Configuracion de su cámara personalizada
         */
        val camaraPersonalizada = CameraPosition.Builder()
            .target(univalle) //donde apunta la cámara
            .zoom(17f) // 15 y 18 calles  20 edificios
            .tilt(45f) //ángulo de inclinación de la cámara, no deberian ser agresivos con los ángulos de la camara
            .bearing(195f) //cambio de orientación de 0 a 360
            .build()
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camaraPersonalizada))

        /**
         * Movimiento de la cámara (animacion de la cámara)
         * Plus: uso estandar de corrutinas
         */
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(univalle, 17f))
        //Corrutinas para apreciar mejor el movimiento
        /*lifecycleScope.launch{
            delay(5000)
            //Para mover la cámara entre puntos en el mapa
            //les recomiendo usar una animación que haga una transición
            //de movimiento... se usa el metodo
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stadium, 17f))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(casitaJhere, 17f))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(valleLuna, 17f))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(salchiSalvaje, 17f))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jardinBotanico, 17f))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(plazaQuezada, 17f))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(plazaMurillo, 17f))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(canchaMinera, 17f))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miCasa, 17f))
            delay(2000)
            mMap.addMarker(MarkerOptions()
                .position(miCasa)
                .title("Destino final")
                .snippet("${miCasa.latitude},${miCasa.longitude}"))
        }*/

        /**
         * Movimiento de camra por pixeles
         * que puede ser horizontal, vertical o combinado
         */
        /*lifecycleScope.launch{
            delay(5_000)
            for (i in 0..100){
                mMap.animateCamera(CameraUpdateFactory.scrollBy(0f,140f))
                delay(500)
            }
        }*/

        /**
         * Bounds para delimitar areas de acción
         * en el mapa, armar sesgos.
         */
        val univalleBounds = LatLngBounds(plazaAbaroa, cementerioJudios)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lapaz, 15f))
        lifecycleScope.launch{
            delay(3_500)
            //Punto central del cuadrante definido
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(univalleBounds.center,10f))
            delay(2000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(univalleBounds,Utils.dp(32)))
        }
        mMap.setLatLngBoundsForCameraTarget(univalleBounds)



        //Activar la posicion actual en el mapa
        //Evaluar permisos de GPS...
        mMap.isMyLocationEnabled = true



        //Evento de click sobre el mapa
        mMap.setOnMapClickListener {
            if(contadorMarcador<5){
                contadorMarcador++
                mMap.addMarker(MarkerOptions()
                    .position(it)
                    .title("Random Place")
                    .snippet("${it.latitude},${it.longitude}")
                    .draggable(true))
            }else{
                Toast.makeText(this,"Limite de marcadores alcanzados", Toast.LENGTH_SHORT).show()
            }

        }
        /**
         *Configuracion de controles de UI
         * y Gestures del mapa
         */
        mMap.uiSettings.apply {
            isMyLocationButtonEnabled = true //actica el boton
            isZoomControlsEnabled = true //controles de zoom
            isCompassEnabled = true //habilita el compas de la orientacion
            isMapToolbarEnabled = true //botones complemetarios del mapa
            isRotateGesturesEnabled = false //ya no sepuede rotar el mapa
            isTiltGesturesEnabled = false //ya no pueden inclinar la pantalla
            isZoomGesturesEnabled = true //habilitar o deshabilitar gestore de zoom
        }

        /**
         * configuracion personalizacion estilos del mapa
         */
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        /**
         * Configuracion y personalizacion de marcadores
         * Estilos, formas y eventos
         */
        val univalleMarcador = mMap.addMarker(
            MarkerOptions()
                .title("Mi universidad")
                .position(univalle)
        )
        univalleMarcador?.run {
            //setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) //CAMBIOS DE COLOR EN BASE A TONOS DEFINIDOS EN ANDROID
            //setIcon(BitmapDescriptorFactory.defaultMarker(200f))//cambio de color hue perzonalizado
            /*Utils.getBitmapFromVector(this@MapsActivity,R.drawable.ic_baseline_minor_crash_24)?.let {
                setIcon(BitmapDescriptorFactory.fromBitmap(it))
            }*/     //Marcador perzonalizado a partir de imagenes vectoriales de la libreria de android
            setIcon(BitmapDescriptorFactory.fromResource(R.drawable.fastfood))
            rotation=220f
            setAnchor(0.5f,0.5f) //punto de rotacion central
            isFlat = true //el marcador rota o no con el mapa
            isDraggable = true // Se puede arrastrar el marcador
            snippet = "Texto alternativo"
        }
        //Eventos en markers
        mMap.setOnMarkerClickListener(this)
    }
    private fun setupToggleButtons(){
        binding.toggleGroup.addOnButtonCheckedListener {
                group, checkedId, isChecked ->
            if(isChecked){
                mMap.mapType = when(checkedId){
                    R.id.btnNormal -> GoogleMap.MAP_TYPE_NORMAL
                    R.id.btnHibrido -> GoogleMap.MAP_TYPE_HYBRID
                    R.id.btnSatelital -> GoogleMap.MAP_TYPE_SATELLITE
                    R.id.btnTerreno -> GoogleMap.MAP_TYPE_TERRAIN
                    else -> GoogleMap.MAP_TYPE_NONE
                }
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        //marker es el marcador al que le hace click
        Toast.makeText(this,"${marker.position.latitude}, ${marker.position.longitude}",Toast.LENGTH_LONG).show()
        return false
    }
}