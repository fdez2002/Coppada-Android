package com.fdez.projecttfg.ui.detalleNegocio

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.fdez.projecttfg.Api.YelpApi
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.FragmentDetalleNegocioBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
            findNavController().navigateUp()

        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            val cadena = arguments?.getString("cadena")

            val negocioDetalle = YelpApi().getBusinessDetails(cadena.toString())
            Log.d("tag", negocioDetalle.toString())



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
                    binding.tvPaginaWebN.text = negocioDetalle.url
                    binding.tvContactoN.text= negocioDetalle.phone
                }
            }
            val businesses = negocioDetalle?.let { YelpApi().getBusinessLocation(negocioDetalle.alias) };
            Log.d("tag", businesses.toString())
        }



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