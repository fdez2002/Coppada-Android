package com.fdez.projecttfg.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdez.projecttfg.Api.OnItemClickListenerNegocio
import com.fdez.projecttfg.Api.YelpApi
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.R
import com.fdez.projecttfg.adapters.NegocioAdapter
import com.fdez.projecttfg.databinding.FragmentFavoriteBinding
import com.fdez.projecttfg.ui.detalleNegocio.DetalleNegocioFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    //This property is only valid between onCreateView and
    //onDestroyView.
    private val binding get() = _binding!!
    private val firebase: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
    private var negocioList: List<Negocio>? = null
    private var bottomNavigationView: BottomNavigationView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()

        val db = FirebaseFirestore.getInstance()


        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            // Manejar la excepción de manera adecuada, por ejemplo, imprimir el mensaje de error
            Log.e(tag, "Excepción en la corutina: $exception")
        }

        if (userId != null) {
            val offersCollection = FirebaseFirestore.getInstance().collection("likes")
            val negocioList = mutableListOf<Negocio>()

            offersCollection
                .whereEqualTo("id_user", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val alias = document.getString("alias")
                        if (alias != null) {
                            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                                val negocio = YelpApi().getAlias(alias)
                                negocioList.add(negocio)
                                //Toast.makeText(requireContext(), alias, Toast.LENGTH_SHORT).show()
                                withContext(Dispatchers.Main) {
                                    //Configurar RecyclerView y Adapter
                                    val recyclerView = binding.rvLikes
                                    recyclerView.layoutManager = LinearLayoutManager(context)
                                    val adapter = negocioList?.let { NegocioAdapter(it, null, requireContext(), true) }
                                    recyclerView.adapter = adapter

                                    adapter?.setOnItemClickListener(object :
                                        OnItemClickListenerNegocio {
                                        override fun onItemClick(negocio: Negocio) {
                                            val nombreNegocioAlias = negocio.alias ?: return

                                            val bundle = Bundle()
                                            bundle.putString("cadena", nombreNegocioAlias)

                                            val fragment = DetalleNegocioFragment.newInstance(nombreNegocioAlias)
                                            fragment.arguments = bundle
                                            findNavController().navigate(
                                                R.id.action_navigation_favorite_to_detalleNegocioFragment,
                                                bundle
                                            )
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejar la falla de la obtención de los documentos
                    Log.d(tag, exception.toString())
                }
        }





        return root
    }
    override fun onResume() {
        super.onResume()
        bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.visibility = View.VISIBLE

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}