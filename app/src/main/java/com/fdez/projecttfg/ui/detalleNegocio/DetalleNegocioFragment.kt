package com.fdez.projecttfg.ui.detalleNegocio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.FragmentAccountBinding
import com.fdez.projecttfg.databinding.FragmentDetalleNegocioBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class DetalleNegocioFragment : Fragment() {
    private var _binding: FragmentDetalleNegocioBinding? = null
    private var bottomNavigationView: BottomNavigationView? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentDetalleNegocioBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.visibility = View.GONE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomNavigationView?.visibility = View.VISIBLE
        bottomNavigationView = null

    }
}