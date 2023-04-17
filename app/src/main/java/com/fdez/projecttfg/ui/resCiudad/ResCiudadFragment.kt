package com.fdez.projecttfg.ui.resCiudad


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdez.projecttfg.Api.OnItemClickListenerNegocio
import com.fdez.projecttfg.Api.YelpApi
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.NegocioAdapter
import com.fdez.projecttfg.NegocioAdapterSmall
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.FragmentResCiudadBinding
import com.fdez.projecttfg.ui.detalleNegocio.DetalleNegocioFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResCiudadFragment : Fragment() {
    private var _binding: FragmentResCiudadBinding? = null

    private var bottomNavigationView: BottomNavigationView? = null


    private val binding get() = _binding!!
    private var negocioListBYR: List<Negocio>? = null
    private var negocioListCafeTe: List<Negocio>? = null
    private var negocioListBakeri: List<Negocio>? = null
    private var negocioListCopas: List<Negocio>? = null
    private var negocioListFatsFood: List<Negocio>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentResCiudadBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.topAppBarRec.setOnClickListener {
            findNavController().navigateUp()

        }

        mostrarResBar()
        mostrarCafeTe()
        mostrarBakeries()
        mostrarCopas()
        mostrarFastFood()
        mostrarOil()

        return root
    }
    private fun mostrarResBar(){
        CoroutineScope(Dispatchers.IO).launch {

            negocioListBYR = YelpApi().search("restaurantes,bars", "Madrid")
            withContext(Dispatchers.Main) {
                //Configurar RecyclerView y Adapter
                val recyclerView = binding.rvRYB
                recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val adapter = negocioListBYR?.let { NegocioAdapterSmall(it) }
                recyclerView.adapter = adapter

                adapter?.let { navegarDetallebuss(it) }
            }
        }

    }
    private fun mostrarCafeTe(){
        CoroutineScope(Dispatchers.IO).launch {

            negocioListCafeTe = YelpApi().search("Coffee & Tea", "Madrid")
            withContext(Dispatchers.Main) {
                //Configurar RecyclerView y Adapter
                val recyclerView = binding.rvCafeTe
                recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val adapter = negocioListCafeTe?.let { NegocioAdapterSmall(it) }
                recyclerView.adapter = adapter

                adapter?.let { navegarDetallebuss(it) }
            }
        }
    }
    private fun mostrarBakeries(){
        CoroutineScope(Dispatchers.IO).launch {

            negocioListBakeri = YelpApi().search("Bakeries", "Madrid")
            withContext(Dispatchers.Main) {
                //Configurar RecyclerView y Adapter
                val recyclerView = binding.rvBaker
                recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val adapter = negocioListBakeri?.let { NegocioAdapterSmall(it) }
                recyclerView.adapter = adapter

                adapter?.let { navegarDetallebuss(it) }
            }
        }
    }
    private fun mostrarCopas(){
        CoroutineScope(Dispatchers.IO).launch {

            negocioListCopas = YelpApi().search("bars", "Madrid")
            withContext(Dispatchers.Main) {
                //Configurar RecyclerView y Adapter
                val recyclerView = binding.rvCopas
                recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val adapter = negocioListCopas?.let { NegocioAdapterSmall(it) }
                recyclerView.adapter = adapter

                adapter?.let { navegarDetallebuss(it) }
            }
        }
    }
    private fun mostrarFastFood(){
        CoroutineScope(Dispatchers.IO).launch {

            negocioListFatsFood = YelpApi().search("Fast Food,Burgers,Pizza", "Madrid")
            withContext(Dispatchers.Main) {
                //Configurar RecyclerView y Adapter
                val recyclerView = binding.rvFastFood
                recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val adapter = negocioListFatsFood?.let { NegocioAdapterSmall(it) }
                recyclerView.adapter = adapter

                adapter?.let { navegarDetallebuss(it) }
            }
        }
    }
    private fun mostrarOil(){
        CoroutineScope(Dispatchers.IO).launch {

            negocioListFatsFood = YelpApi().search("Gasolineras", "Madrid")
            withContext(Dispatchers.Main) {
                //Configurar RecyclerView y Adapter
                val recyclerView = binding.rvOil
                recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val adapter = negocioListFatsFood?.let { NegocioAdapterSmall(it) }
                recyclerView.adapter = adapter

                adapter?.let { navegarDetallebuss(it) }
            }
        }
    }
    private fun navegarDetallebuss(adapter: NegocioAdapterSmall){
        adapter?.setOnItemClickListener(object : OnItemClickListenerNegocio {
            override fun onItemClick(negocio: Negocio) {
                val nombreNegocioAlias = negocio.alias ?: return

                val bundle = Bundle()
                bundle.putString("cadena", nombreNegocioAlias)

                val fragment = DetalleNegocioFragment.newInstance(nombreNegocioAlias)
                fragment.arguments = bundle
                findNavController().navigate(
                    R.id.action_resCiudadFragment_to_detalleNegocioFragment,
                    bundle
                )
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.visibility = View.GONE


    }
    companion object {
        fun newInstance(ciudad: String): ResCiudadFragment {
            val args = Bundle()
            args.putString("ciudad", ciudad)
            val fragment = ResCiudadFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}