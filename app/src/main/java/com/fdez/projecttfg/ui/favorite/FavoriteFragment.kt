package com.fdez.projecttfg.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdez.projecttfg.Api.YelpApi
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.adapters.NegocioAdapter
import com.fdez.projecttfg.databinding.FragmentFavoriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val firebase: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
    private var negocioList: List<Negocio>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()

        val db = FirebaseFirestore.getInstance()

        val likesCollection = db.collection("likes")

        val query = likesCollection.whereEqualTo("id_user", auth?.uid)
        query.get().addOnSuccessListener { documents ->
            requireActivity().runOnUiThread {

                for (document in documents) {
                    val alias = document.get("alias")
                    CoroutineScope(Dispatchers.IO).launch {
                        val recyclerView = binding.rvLikes
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        val yelpApi = YelpApi()

                        // Obtener los detalles del negocio por su alias
                        val negocioDetalle = yelpApi.getBusinessDetails(alias as String)

                        // Crear una lista con un solo elemento (el negocio obtenido)
                        val negocioList = mutableListOf<Negocio>()
                        negocioDetalle?.let {
                            negocioList.add(
                                Negocio(
                                    it.image_url, it.is_closed,
                                    it.name, it.coordinates, it.rating,
                                    it.review_count, it.alias
                                )
                            )
                        }

                        withContext(Dispatchers.Main) {
                            // Configurar el RecyclerView y el adapter con la lista de negocios
                            val adapter = NegocioAdapter(negocioList)
                            recyclerView.adapter = adapter
                        }
                    }

                }
            }
        }.addOnFailureListener { exception ->
            // Aquí puedes manejar el error
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}