package com.fdez.projecttfg.ui.map

import android.os.Bundle
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fdez.projecttfg.Api.YelpApi
import com.fdez.projecttfg.managerCache.CacheManager
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null

    val negocioList = mutableListOf<Negocio>()

    private var isDataLoaded = false

    private val cache = LruCache<String, List<Negocio>>(1024)

    private lateinit var mMap: GoogleMap


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this@MapFragment)
        return root
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Establecer un OnMarkerClickListener para el mapa
        mMap.setOnMarkerClickListener { marker ->
            // Crear y mostrar un BottomSheet personalizado con la información del marcador
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()


            // Configurar la información del marcador en el BottomSheet
            /*
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val snippetTextView = view.findViewById<TextView>(R.id.snippetTextView)
            titleTextView.text = marker.title
            snippetTextView.text = marker.snippet

             */

            true
        }

        CoroutineScope(Dispatchers.IO).launch {
            val cacheKey = "negocioList"
            val cache = CacheManager(requireContext())

            //Intenta obtener la lista de negocios de la cache
            val cachedNegocioList = cache.loadData<List<Negocio>>(cacheKey)

            if (cachedNegocioList != null && cachedNegocioList.isNotEmpty()) {
                //Si la lista está en la cache, úsala directamente
                negocioList.addAll(cachedNegocioList)

            } else {
                //Si no está en la cache, realiza la llamada a la API
                val negocioListFastFood = YelpApi().search("Fast Food,Burgers,Pizza", "Madrid")
                negocioList.addAll(negocioListFastFood)
                val negocioListRestBar = YelpApi().search("restaurantes,bars", "Madrid")
                negocioList.addAll(negocioListRestBar)
                val negocioListCafeTe = YelpApi().search("Coffee & Tea", "Madrid")
                negocioList.addAll(negocioListCafeTe)
                //val negocioListOil = YelpApi().search("Gasolineras", "Madrid")
                //negocioList.addAll(negocioListOil)
                val negocioListBake = YelpApi().search("Bakeries", "Madrid")
                negocioList.addAll(negocioListBake)

                //Guarda la lista en la cache para futuras consultas
                cache.saveData(cacheKey, negocioList)
            }

            //Si no hay negocios en la lista, no hay nada que hacer
            if (negocioList.isEmpty()) {
                return@launch
            }

            //Agrega un marcador en la ubicación de cada negocio en la lista
            negocioList.forEach { negocio ->
                negocio.coordinates?.let { location ->
                    val latLng = LatLng(location.latitude.toDouble(), location.longitude.toDouble())
                    withContext(Dispatchers.Main) {
                        mMap.addMarker(MarkerOptions().position(latLng).title(negocio.name))
                    }
                }
            }

            //Configura la cámara del mapa
            val builder = LatLngBounds.Builder()
            negocioList.forEach { negocio ->
                negocio.coordinates?.let { location ->
                    val latLng = LatLng(location.latitude.toDouble(), location.longitude.toDouble())
                    withContext(Dispatchers.Main) {
                        builder.include(latLng)
                    }
                }
            }
            val bounds = builder.build()
            val padding = 100
            withContext(Dispatchers.Main) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}