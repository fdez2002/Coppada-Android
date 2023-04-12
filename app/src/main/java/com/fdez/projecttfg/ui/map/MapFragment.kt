package com.fdez.projecttfg.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null

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


        mapFragment.getMapAsync(this)

        return root
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Configura la cámara del mapa
        val builder = LatLngBounds.Builder()
        val sanFrancisco = LatLng(37.7749, -122.4194)
        builder.include(sanFrancisco) // incluye San Francisco en el builder
        val newYork = LatLng(40.7128, -74.0060)
        builder.include(newYork) // incluye Nueva York en el builder
        val paris = LatLng(48.8566, 2.3522)
        builder.include(paris) // incluye París en el builder
        val bounds = builder.build()
        val padding = 100 // ajusta el padding según tus necesidades
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))

        // Agrega un marcador en San Francisco
        mMap.addMarker(MarkerOptions().position(sanFrancisco).title("San Francisco"))

        // Agrega un marcador en Nueva York
        mMap.addMarker(MarkerOptions().position(newYork).title("Nueva York"))

        // Agrega un marcador en París
        mMap.addMarker(MarkerOptions().position(paris).title("París"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}