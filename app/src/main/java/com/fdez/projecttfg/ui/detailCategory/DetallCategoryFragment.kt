package com.fdez.projecttfg.ui.detailCategory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdez.projecttfg.api.OnItemClickListenerNegocio
import com.fdez.projecttfg.api.YelpApi
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.adapters.NegocioAdapter
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.FragmentDetallCategoryBinding
import com.fdez.projecttfg.ui.detalleNegocio.DetalleNegocioFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetallCategoryFragment : Fragment() {

    // Variable global para almacenar la lista de negocios
    private var negocioList: List<Negocio>? = null

    // Booleano para comprobar si la lista de negocios ya ha sido obtenida de la API
    private var isDataLoaded = false
    private var _binding: FragmentDetallCategoryBinding? = null

    private var bottomNavigationView: BottomNavigationView? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetallCategoryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val titulo = arguments?.getString("titulo")
        binding.topAppBar.title = titulo

        cargarRV()

        binding.topAppBar.setOnClickListener {
            findNavController().navigateUp()

        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.visibility = View.GONE



    }

    private fun cargarRV() {
        // Si la lista de negocios no ha sido inicializada previamente, se obtienen los datos de la API
        if (!isDataLoaded) {

            CoroutineScope(Dispatchers.IO).launch {
                val cadena = arguments?.getString("category")
                negocioList = YelpApi().search(cadena.toString(), "Madrid")
                try{
                    withContext(Dispatchers.Main) {
                        // Configurar RecyclerView y Adapter
                        val recyclerView = binding.rvNegocios2
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        val adapter = negocioList?.let { NegocioAdapter(it, null, requireContext(), false) }

                        recyclerView.adapter = adapter

                        adapter?.setOnItemClickListener(object : OnItemClickListenerNegocio {
                            override fun onItemClick(negocio: Negocio) {
                                val nombreNegocioAlias = negocio.alias

                                val bundle = Bundle()
                                bundle.putString("cadena", nombreNegocioAlias)
                                val fragment =
                                    DetalleNegocioFragment.newInstance(nombreNegocioAlias)
                                fragment.arguments = bundle
                                findNavController().navigate(
                                    R.id.action_detallCategoryFragment_to_detalleNegocioFragment,
                                    bundle
                                )
                            }
                        })
                        isDataLoaded = true // La lista de negocios ya ha sido cargada
                    }
                }catch (e:Exception){}

            }
        } else {
            // Si la lista de negocios ya ha sido inicializada, se configura el RecyclerView con los datos ya cargados
            val recyclerView = binding.rvNegocios2
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = NegocioAdapter(negocioList!!, null, requireContext(), false)
            recyclerView.adapter = adapter

            adapter.setOnItemClickListener(object : OnItemClickListenerNegocio {
                override fun onItemClick(negocio: Negocio) {
                    val nombreNegocioAlias = negocio.alias
                    val bundle = Bundle()
                    bundle.putString("cadena", nombreNegocioAlias)
                    val fragment = DetalleNegocioFragment.newInstance(nombreNegocioAlias)
                    fragment.arguments = bundle
                    findNavController().navigate(
                        R.id.action_detallCategoryFragment_to_detalleNegocioFragment,
                        bundle
                    )
                }
            })

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        try {
            _binding = null
            //bottomNavigationView?.visibility = View.VISIBLE
            //bottomNavigationView = null
        } catch (e: Exception) {}
    }

    companion object {
        fun newInstance(cadena: String, titulo: String): DetallCategoryFragment {
            val args = Bundle()
            args.putString("cadena", cadena)
            args.putString("titulo", titulo)
            val fragment = DetallCategoryFragment()
            fragment.arguments = args
            return fragment
        }
    }


}