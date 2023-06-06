package com.fdez.projecttfg.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fdez.projecttfg.api.YelpApi
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.FragmentMapBinding
import com.fdez.projecttfg.managerCache.CacheManager
import com.fdez.projecttfg.ui.detalleNegocio.DetalleNegocioFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
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

    private var nombreNegocioAlias: String? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var bottomNavigationView: BottomNavigationView? = null


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Ocultar el NestedScrollView
        //val nestedScrollView = requireActivity().findViewById<NestedScrollView>(R.id.nestedScrollView)

        //nestedScrollView.visibility = View.GONE
    }


    @SuppressLint("MissingInflatedId")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        CoroutineScope(Dispatchers.IO).launch {
            val cacheKey = "negocioList"
            val cache = CacheManager(requireContext())

            //Intenta obtener la lista de negocios de la cache
            val cachedNegocioList = cache.loadData<List<Negocio>>(cacheKey)

            if (!cachedNegocioList.isNullOrEmpty()) {
                //Si la lista está en la cache se úsala directamente
                negocioList.addAll(cachedNegocioList)

            } else {
                try {
                    //Si no está en la cache, realiza la llamada a la API
                    val negocioListFastFood = YelpApi().search("Fast Food,Burgers,Pizza", "Madrid")
                    //val negocioListFastFood = YelpApi().search("Fast Food,Burgers,Pizza", requireContext())
                    negocioList.addAll(negocioListFastFood)
                    val negocioListRestBar = YelpApi().search("restaurantes,bars", "Madrid")
                    //val negocioListRestBar = YelpApi().search("restaurantes,bars", requireContext())
                    negocioList.addAll(negocioListRestBar)
                    val negocioListCafeTe = YelpApi().search("Coffee & Tea", "Madrid")
                    //val negocioListCafeTe = YelpApi().search("Coffee & Tea", requireContext())
                    negocioList.addAll(negocioListCafeTe)
                    //val negocioListOil = YelpApi().search("Gasolineras", "Madrid")
                    //negocioList.addAll(negocioListOil)
                    val negocioListBake = YelpApi().search("Bakeries", "Madrid")
                    //val negocioListBake = YelpApi().search("Bakeries", requireContext())
                    negocioList.addAll(negocioListBake)

                    //Guarda la lista en la cache para futuras consultas
                    cache.saveData(cacheKey, negocioList)
                }catch (ex: Exception){}

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
        //Establecer un OnMarkerClickListener para el mapa
        mMap.setOnMarkerClickListener { marker ->
            //Obtener el título del marcador seleccionado
            val title = marker.title

            //Obtener el negocio asociado al título del marcador
            val selectedNegocio = negocioList.find { negocio ->
                negocio.name == title
            }

            //Crear y mostrar un BottomSheet personalizado con la información del marcador
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            //Configurar la información del marcador en el BottomSheet
            val titleTextView = view.findViewById<TextView>(R.id.tv_nombreBS)
            val image = view.findViewById<ImageView>(R.id.img_negocio)
            val ranting = view.findViewById<RatingBar>(R.id.ratingBarBS)
            val cardDetail = view.findViewById<CardView>(R.id.cardDetail)
            val tvreviewcountBT = view.findViewById<TextView>(R.id.tv_review_countBT)

            selectedNegocio?.let {
                Glide.with(binding.root.context)
                    .load(it.image_url)
                    .error(com.denzcoskun.imageslider.R.drawable.error)
                    .into(image)
                titleTextView.text = it.name
                tvreviewcountBT.text = it.review_count.toString()

                ranting.rating = it.rating.toFloat()
                nombreNegocioAlias = it.alias

            }
            cardDetail.setOnClickListener {
                bottomSheetDialog.dismiss()

                val bundle = Bundle()
                bundle.putString("cadena", nombreNegocioAlias)

                val fragment = DetalleNegocioFragment.newInstance(nombreNegocioAlias.toString())
                fragment.arguments = bundle
                findNavController().navigate(
                    R.id.action_navigation_map_to_detalleNegocioFragment,
                    bundle
                )
            }

            true
        }

    }
    override fun onResume() {
        super.onResume()
        bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}