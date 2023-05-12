package com.fdez.projecttfg.ui.detalleNegocio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.fdez.projecttfg.Api.YelpApi
import com.fdez.projecttfg.R
import com.fdez.projecttfg.Review
import com.fdez.projecttfg.adapters.NegocioAdapter
import com.fdez.projecttfg.adapters.ReviewAdapter
import com.fdez.projecttfg.databinding.FragmentDetalleNegocioBinding
import com.fdez.projecttfg.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetalleNegocioFragment : Fragment(), OnMapReadyCallback {
    private var bottomNavigationView: BottomNavigationView? = null

    private var _binding: FragmentDetalleNegocioBinding? = null
    private val binding get() = _binding!!

    private lateinit var negocioReviwsList: List<Review>

    private var number: String? = null
    private var web: String? = null

    private lateinit var mMap: GoogleMap

    private var latLngBusines: LatLng? = LatLng(0.0, 0.0)

    private var alias: String? = null

    private var reviewsList: List<Review>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetalleNegocioBinding.inflate(inflater, container, false)
        try {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map_viewBussis) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }catch (ex: Exception){
            Log.d(tag, ex.toString())
        }

        cargarDatos()
        obtenerReviews()

        binding.toolbarBackButton.setOnClickListener {
            val navController = findNavController()
            val currentDestination = navController.currentDestination

            if (currentDestination?.id == R.id.action_navigation_home_to_detalleNegocioFragment) {
                bottomNavigationView?.visibility = View.VISIBLE
                bottomNavigationView = null
            }
            findNavController().navigateUp()

        }
        binding.extendedFabLlmar.setOnClickListener {
            val phoneNumber = number
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }
        binding.extendedFabWeb.setOnClickListener {
            val url = web
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }


        return binding.root
    }
    private fun cargarDatos(){
        val cadena = arguments?.getString("cadena")
        alias = cadena


        try {
            CoroutineScope(Dispatchers.IO).launch {
                val negocioDetalle = YelpApi().getBusinessDetails(cadena.toString())


                if (negocioDetalle != null) {
                    withContext(Dispatchers.Main) {
                        val imageList = ArrayList<SlideModel>() // Create image list
                        for (photoUrl in negocioDetalle.photos) {
                            val slideModel = SlideModel(photoUrl)
                            imageList.add(slideModel)
                        }
                        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
                        binding.tvNombreN.text = negocioDetalle.name
                        binding.ratingBar2.rating = negocioDetalle.rating.toFloat()
                        web = negocioDetalle.url
                        //binding.tvPaginaWebN.text = negocioDetalle.url
                        number = negocioDetalle.phone
                        //binding.tvContactoN.text = negocioDetalle.phone
                        negocioDetalle.coordinates?.let { location ->
                            latLngBusines =
                                LatLng(location.latitude.toDouble(), location.longitude.toDouble())
                            setupMap()
                        }

                    }
                }

            }
        }catch (ex: Exception){
            Log.d(tag, ex.toString())
        }


    }
    private fun obtenerReviews(){
        CoroutineScope(Dispatchers.IO).launch {

            //Si no está en la cache, realiza la llamada a la API
            negocioReviwsList = YelpApi().getBusinessReviews(alias.toString())
            Log.d("revies", negocioReviwsList.toString())
            withContext(Dispatchers.Main) {
                // Detectamo si la lista esta vacia
                if(negocioReviwsList.isNotEmpty()) {
                    //Configurar RecyclerView y Adapter
                    val recyclerView = binding.recyReviews
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    val adapter = ReviewAdapter(negocioReviwsList)
                    recyclerView.adapter = adapter
                }else{
                    binding.textView10.text = "No tiene reviews"
                }
            }

        }


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.visibility = View.GONE


    }

    override fun onMapReady(googleMap: GoogleMap) {
        //Callback cuando el mapa esté listo para ser utilizado
        mMap = googleMap

    }

    private fun setupMap() {
        try {
            mMap?.addMarker(
                MarkerOptions()
                    .position(latLngBusines!!)
            )
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBusines!!, 15.0f))

        }catch (ex: Exception){
            Log.d(tag, ex.toString())
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


    }

    companion object {
        fun newInstance(cadena: String): DetalleNegocioFragment {
            val args = Bundle()
            args.putString("cadena", cadena)
            val fragment = DetalleNegocioFragment()
            fragment.arguments = args
            return fragment
        }
    }
}