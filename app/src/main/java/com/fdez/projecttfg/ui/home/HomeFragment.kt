package com.fdez.projecttfg.ui.home

import android.content.ContentValues.TAG
import android.content.Context
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fdez.projecttfg.Api.OnItemClickListenerNegocio
import com.fdez.projecttfg.Api.YelpApi
import com.fdez.projecttfg.BusinessSearchResponse
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.NegocioAdapter
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.FragmentHomeBinding
import com.fdez.projecttfg.ui.detalleNegocio.DetalleNegocioFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class HomeFragment : Fragment() {

    // Variable global para almacenar la lista de negocios
    private var negocioList: List<Negocio>? = null

    // Booleano para comprobar si la lista de negocios ya ha sido obtenida de la API
    private var isDataLoaded = false

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root




        // Si la lista de negocios no ha sido inicializada previamente, se obtienen los datos de la API
        if (!isDataLoaded) {
            CoroutineScope(Dispatchers.IO).launch {
                negocioList = YelpApi().search("pizza", "Madrid")
                Log.d("tag", negocioList.toString())
                withContext(Dispatchers.Main) {
                    // Configurar RecyclerView y Adapter
                    val recyclerView = binding.rvNegocios
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    val adapter = negocioList?.let { NegocioAdapter(it) }
                    recyclerView.adapter = adapter

                    adapter?.setOnItemClickListener(object : OnItemClickListenerNegocio {
                        override fun onItemClick(negocio: Negocio) {
                            val nombreNegocioAlias = negocio.alias

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
                isDataLoaded = true // La lista de negocios ya ha sido cargada
            }
        } else {
            // Si la lista de negocios ya ha sido inicializada, se configura el RecyclerView con los datos ya cargados
            val recyclerView = binding.rvNegocios
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = negocioList?.let { NegocioAdapter(it) }
            recyclerView.adapter = adapter

            adapter?.setOnItemClickListener(object : OnItemClickListenerNegocio {
                override fun onItemClick(negocio: Negocio) {
                    val nombreNegocioAlias = negocio.alias

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

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
