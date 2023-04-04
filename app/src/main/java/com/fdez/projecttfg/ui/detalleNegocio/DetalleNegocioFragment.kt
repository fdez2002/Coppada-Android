package com.fdez.projecttfg.ui.detalleNegocio

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fdez.projecttfg.R
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

        binding.toolbarBackButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_detalleNegocioFragment_to_navigation_home)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.visibility = View.GONE
        val cadena = arguments?.getString("cadena")
        Toast.makeText(context, "Nombre del negocio: $cadena", Toast.LENGTH_SHORT).show()


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomNavigationView?.visibility = View.VISIBLE
        bottomNavigationView = null

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