package com.fdez.projecttfg.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdez.projecttfg.*
import com.fdez.projecttfg.Api.OnItemClickListenerNegocio
import com.fdez.projecttfg.Api.YelpApi
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


class HomeFragment : Fragment() {

    // Variable global para almacenar la lista de negocios
    private var negocioList: List<Negocio>? = null

    // Booleano para comprobar si la lista de negocios ya ha sido obtenida de la API
    private var isDataLoaded = false

    private var bottomNavigationView: BottomNavigationView? = null


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cargarRV()


        binding.searchview.editText.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val ciudad = binding.searchview.text?.trim().toString()
                val bundle = Bundle()
                bundle.putString("ciudad", ciudad)

                val fragment = DetalleNegocioFragment.newInstance(ciudad)
                fragment.arguments = bundle
                findNavController().navigate(
                    R.id.action_navigation_home_to_resCiudadFragment,
                    bundle
                )
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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    private fun cargarRV() {
        val cacheKey = "negocioListFavo"
        val cache = CacheManager(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            //Intenta obtener la lista de negocios de la cache
            val cachedNegocioList = cache.loadData<List<Negocio>>(cacheKey)

            if (cachedNegocioList != null && cachedNegocioList.isNotEmpty()) {
                //Si la lista está en la cache, úsala directamente
                negocioList = cachedNegocioList
                isDataLoaded = true

            } else {
                //Si no está en la cache, realiza la llamada a la API
                negocioList = YelpApi().search("pizza", "Madrid")

                //Guarda la lista en la cache para futuras consultas
                cache.saveData(cacheKey, negocioList)
                isDataLoaded = true // La lista de negocios ya ha sido cargada
            }

            withContext(Dispatchers.Main) {
                //Configurar RecyclerView y Adapter
                val recyclerView = binding.rvNegocios
                recyclerView.layoutManager = LinearLayoutManager(context)
                val adapter = negocioList?.let { NegocioAdapter(it) }
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
        }
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
    }

}
