package com.fdez.projecttfg.adapters

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdez.projecttfg.Api.OnItemClickListenerNegocio
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.databinding.ItemCardLocalesBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.like.LikeButton
import com.like.OnLikeListener


class NegocioAdapter(
    private val negocios: List<Negocio>,
    private var listener: OnItemClickListenerNegocio? = null
) :
    RecyclerView.Adapter<NegocioAdapter.NegocioViewHolder>() {
    private var firebaseFireStore: FirebaseFirestore? = null


    inner class NegocioViewHolder(private val binding: ItemCardLocalesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(negocio: Negocio) {
            Glide.with(binding.root.context)
                .load(negocio.image_url)
                .error(com.denzcoskun.imageslider.R.drawable.error)
                .into(binding.imgNegocio)
            binding.tvNombre.text = negocio.name
            //binding.tvLugar.text = negocio.coordinates!!.city
            binding.tvReviewCount.text = negocio.review_count.toString()
            if (negocio.is_closed) {
                //binding.ivIsClosed.setImageResource(R.drawable.closed);
            } else {
                //binding.ivIsClosed.setImageResource(R.drawable.open);
            }
            binding.ratingBar.rating = negocio.rating.toFloat();

            binding.root.setOnClickListener {
                listener?.onItemClick(negocio)
            }

            binding.likeButton.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {

                    //Guarda el alias del usuario en la base de datos
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val alias = negocio.alias // Aquí debes usar el alias que el usuario haya ingresado

                    val db = Firebase.firestore
                    val offersCollection = db.collection("likes")
                    val offer = hashMapOf(
                        "id_user" to userId.toString(),
                        "alias" to alias
                    )
                    offersCollection.add(offer)
                        .addOnSuccessListener {
                            Log.d(TAG, "Documento agregado con ID: \${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error al agregar el documento", e)
                        }



                }
                override fun unLiked(likeButton: LikeButton) {

                }
            })
        }

    }

    private fun postMascota(name: String, edad: String, color: String) {
        //apuntamos a la base de datos
        firebaseFireStore = FirebaseFirestore.getInstance()
        //insertamos los datos en la base de datos
        val map: MutableMap<String, Any> = HashMap()
        map["nombre"] = name
        map["edad"] = edad
        map["color"] = color
        firebaseFireStore!!.collection("mascotas").add(map).addOnSuccessListener {
            // si va todo bien se ejecuta
        }.addOnFailureListener {
            // si hay algun error se iría a este método
            Log.d("subida", "FALLO")
        }

    }

    fun setOnItemClickListener(listener: OnItemClickListenerNegocio) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NegocioViewHolder {
        val binding =
            ItemCardLocalesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NegocioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NegocioViewHolder, position: Int) {
        holder.bind(negocios[position])
    }

    override fun getItemCount(): Int = negocios.size


}