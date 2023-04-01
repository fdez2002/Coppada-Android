package com.fdez.projecttfg.ui.home

import android.content.ContentValues.TAG
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdez.projecttfg.Api.YelpApi
import com.fdez.projecttfg.BusinessSearchResponse
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.NegocioAdapter
import com.fdez.projecttfg.databinding.FragmentHomeBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException



class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private  var negocioList: List<Negocio>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Obtener datos de la API y guardarlos en la lista de negocios
        CoroutineScope(Dispatchers.IO).launch {
            val negocios = YelpApi().search("pizza", "Linares")
            Log.d("MiApp", "Negocios: $negocios")

            //negocioList = YelpApi().search("pizza", "Linares")
            negocioList = YelpApi().search("pizza", "Madrid")
            withContext(Dispatchers.Main) {
                // Configurar RecyclerView y Adapter
                val recyclerView = binding.rvNegocios
                recyclerView.layoutManager = LinearLayoutManager(context)
                val adapter = negocioList?.let { NegocioAdapter(it) }
                recyclerView.adapter = adapter
            }

        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}