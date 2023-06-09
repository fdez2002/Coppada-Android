package com.fdez.projecttfg.ui.home

import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdez.projecttfg.api.OnItemClickListenerNegocio
import com.fdez.projecttfg.api.YelpApi
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.R
import com.fdez.projecttfg.adapters.NegocioAdapter
import com.fdez.projecttfg.databinding.FragmentHomeBinding
import com.fdez.projecttfg.managerCache.CacheManager
import com.fdez.projecttfg.ui.detailCategory.DetallCategoryFragment
import com.fdez.projecttfg.ui.detalleNegocio.DetalleNegocioFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class HomeFragment : Fragment() {

    // Variable global para almacenar la lista de negocios
    private var negocioList: List<Negocio>? = null

    // Booleano para comprobar si la lista de negocios ya ha sido obtenida de la API
    private var isDataLoaded = false

    private var bottomNavigationView: BottomNavigationView? = null


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val locationPermissionCode = 1
    private var locationString: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if(locationString != ""){
            cargarRV(locationString)
        }else{
            obtenerUbicacion()
        }

        binding.searchview.editText.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarCiudad()
                true
            } else {
                binding.searchBar.text = binding.searchview.text
                binding.searchview.hide()
                false
            }
        }


        binding.buttonRestaBar.setOnClickListener {
            val categoria = "restaurantes,bars"
            val titulo = "Bares y restaurantes"
            navegarCategory(categoria, titulo)

        }
        binding.buttonCafeTe.setOnClickListener {
            val categoria = "Coffee & Tea"
            val titulo = "Café y té"
            navegarCategory(categoria, titulo)
        }
        binding.buttonCopas.setOnClickListener {
            val categoria = "bars"
            val titulo = "De copas"
            navegarCategory(categoria, titulo)
        }
        binding.buttonRepDomici.setOnClickListener {
            val categoria = "fast food"
            val titulo = "Fast Food"
            navegarCategory(categoria, titulo)
        }
        binding.buttonOil.setOnClickListener {
            val categoria = "Gasolineras"
            val titulo = "Gasolineras"
            navegarCategory(categoria, titulo)
        }
        binding.buttonPasteleri.setOnClickListener {
            val categoria = "Bakeries"
            val titulo = "Pastelerias"
            navegarCategory(categoria, titulo)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            val cache = CacheManager(requireContext())
            cache.clearCache()
            isDataLoaded = false
            //cargarRV()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return root
    }
    private fun buscarCiudad(){
        val ciudad = binding.searchview.text?.trim().toString()
        val bundle = Bundle()
        bundle.putString("ciudad", ciudad)

        val fragment = DetalleNegocioFragment.newInstance(ciudad)
        fragment.arguments = bundle
        findNavController().navigate(
            R.id.action_navigation_home_to_resCiudadFragment,
            bundle
        )

    }


    private fun navegarCategory(categoria: String, titulo: String){
        val bundle = Bundle()
        bundle.putString("category", categoria)
        bundle.putString("titulo", titulo)
        val fragment = DetallCategoryFragment.newInstance(categoria, titulo)
        fragment.arguments = bundle
        findNavController().navigate(
            R.id.action_navigation_home_to_detallCategoryFragment,
            bundle
        )
    }
    private fun obtenerUbicacion() {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permisos al usuario si aún no se han otorgado
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationPermissionCode
            )
        } else {
            // Los permisos ya se han otorgado, obtener la ubicación actual
            obtenerUbicacionActual(locationManager)
        }
    }

    private fun obtenerUbicacionActual(locationManager: LocationManager) {
        // Definir un LocationListener para recibir las actualizaciones de ubicación
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // La ubicación actual se ha obtenido aquí
                val latitude = location.latitude
                val longitude = location.longitude

                // Utilizar la ubicación en la búsqueda de negocios o realizar otras acciones necesarias
                locationString = "$latitude,$longitude"
                cargarRV(locationString)
            }

            // Implementar los demás métodos del LocationListener si es necesario
        }

        // Registrar el LocationListener para recibir actualizaciones de ubicación
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestSingleUpdate(
            LocationManager.GPS_PROVIDER,
            locationListener,
            Looper.getMainLooper()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Los permisos de ubicación han sido otorgados
                val locationManager =
                    activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                obtenerUbicacionActual(locationManager)
            } else {
                // El usuario denegó los permisos de ubicación, manejar esta situación según tus necesidades
            }
        }
    }


    private fun cargarRV(localizacion: String) {

        val cacheKey = "negocioListFavo"
        val cache = CacheManager(requireContext())

        CoroutineScope(Dispatchers.IO ).launch {
            //Intenta obtener la lista de negocios de la cache
            val cachedNegocioList = cache.loadData<List<Negocio>>(cacheKey)


            if (cachedNegocioList != null && cachedNegocioList.isNotEmpty()) {
                //Si la lista está en la cache, úsala directamente
                negocioList = cachedNegocioList
                isDataLoaded = true

            } else {
                //Si no está en la cache, realiza la llamada a la API
                //negocioList = YelpApi().searchCiudad("Fast Food", "Madrid")
                negocioList = YelpApi().search("Fast Food", localizacion)
                Log.d(tag, negocioList.toString())
                //Guarda la lista en la cache para futuras consultas
                cache.saveData(cacheKey, negocioList)
                isDataLoaded = true // La lista de negocios ya ha sido cargada
            }
            //Log.d(tag, negocioList.toString())

            withContext(Dispatchers.Main) {
                llenarRecycler()
            }
        }
    }
    private fun llenarRecycler(){
        //Configurar RecyclerView y Adapter
        val recyclerView = binding.rvNegocios
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = negocioList?.let { NegocioAdapter(it, null, requireContext(), false) }
        recyclerView.adapter = adapter

        adapter?.setOnItemClickListener(object : OnItemClickListenerNegocio {
            override fun onItemClick(negocio: Negocio) {
                val nombreNegocioAlias = negocio.alias ?: return

                val bundle = Bundle()
                bundle.putString("cadena", nombreNegocioAlias)

                val fragment = DetalleNegocioFragment.newInstance(nombreNegocioAlias)
                fragment.arguments = bundle
                findNavController().navigate(
                    R.id.action_navigation_home_to_detalleNegocioFragment,
                    bundle
                )
            }
        })

    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.visibility = View.VISIBLE

        binding.searchview.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //val bundle = arguments ?: Bundle()
        //bundle.putInt("scroll_position", scrollPosition)
        //arguments = bundle
    }

}
