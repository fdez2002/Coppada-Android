package com.fdez.projecttfg.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdez.projecttfg.api.OnItemClickListenerNegocio
import com.fdez.projecttfg.api.YelpApi
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.R
import com.fdez.projecttfg.adapters.NegocioAdapter
import com.fdez.projecttfg.databinding.FragmentFavoriteBinding
import com.fdez.projecttfg.ui.detalleNegocio.DetalleNegocioFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    private val binding get() = _binding!!

    private var auth: FirebaseAuth? = null

    private var bottomNavigationView: BottomNavigationView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = FirebaseAuth.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        configurarManejadorExcepciones()
        // Comprobamos que el usuario este registrado
        if (userId != null) {
            obtenerFavoritos(userId)
        }
        return root
    }

    private fun configurarManejadorExcepciones() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e(tag, "Excepción en la corutina: $exception")
        }
    }

    /**
     * Se un id de usuario y se obtiene los alias de los restaurantes que hay almacenados, se busca en la API y se almacena en una lista
     */
    private fun obtenerFavoritos(userId: String) {
        val offersCollection = FirebaseFirestore.getInstance().collection("likes")
        val negocioList = mutableListOf<Negocio>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val documents = offersCollection
                    .whereEqualTo("id_user", userId)
                    .get()
                    .await()

                for (document in documents) {
                    val alias = document.getString("alias")
                    if (alias != null) {
                        val negocio = YelpApi().getAlias(alias)
                        negocioList.add(negocio)
                    }
                }

                withContext(Dispatchers.Main) {
                    configurarRecyclerView(negocioList)
                }
            } catch (exception: Exception) {
                Log.d(tag, exception.toString())
            }
        }
    }

    /**
     * Obtiene la lista con los negocios y los adapta en el recycler
     */
    private fun configurarRecyclerView(negocioList: List<Negocio>) {
        val recyclerView = binding.rvLikes
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = NegocioAdapter(negocioList, null, requireContext(), true)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : OnItemClickListenerNegocio {
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
