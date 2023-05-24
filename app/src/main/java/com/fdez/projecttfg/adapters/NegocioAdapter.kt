package com.fdez.projecttfg.adapters

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdez.projecttfg.Api.OnItemClickListenerNegocio
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.ItemCardLocalesBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.like.LikeButton
import com.like.OnLikeListener


class NegocioAdapter(
    private val negocios: List<Negocio>,
    private var listener: OnItemClickListenerNegocio? = null ,
    private val context: Context,
    private val isSnackbar: Boolean

) :

    RecyclerView.Adapter<NegocioAdapter.NegocioViewHolder>() {

    private val db = Firebase.firestore
    private val offersCollection = db.collection("likes")



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
                    if(userId != null ) {
                        val offer = hashMapOf(
                            "id_user" to userId.toString(),
                            "alias" to alias,
                            "liked" to "true"
                        )
                        offersCollection.add(offer)
                            .addOnSuccessListener {
                                Log.d(TAG, "Documento agregado con ID: \${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error al agregar el documento", e)
                            }
                    }else{
                        binding.likeButton.isLiked =
                            false

                        //Toast.makeText(context, "Regístrate para tener acceso a todas las funcionalidades.", Toast.LENGTH_SHORT).show()
                        showAlert(context, "","Regístrate para tener acceso a todas las funcionalidades.")
                    }



                }
                override fun unLiked(likeButton: LikeButton) {
                    if(isSnackbar){
                        unlikedSsackbar(negocio,likeButton, adapterPosition)
                    }else{
                        unliked(negocio.alias)
                    }
                }
            })
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val alias = negocio.alias //Aquí debes usar el alias que el usuario haya ingresado

            val query = offersCollection
                .whereEqualTo("id_user", userId.toString())
                .whereEqualTo("alias", alias)

            query.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val liked = document.getString("liked")

                    binding.likeButton.isLiked = liked != null && liked ==  "true" && alias == negocio.alias //Actualiza el estado del botón de "me gusta" en la pantalla
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error al obtener el documento: ", exception)
            }


        }

    }
    fun unliked(alias: String){
        // Elimina el documento de la colección "likes" que corresponde a este usuario y a este negocio
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val query = offersCollection.whereEqualTo("id_user", userId).whereEqualTo("alias", alias)
        query.get().addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Documento eliminado correctamente")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error al eliminar el documento", e)
                    }
            }
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error al obtener el documento: ", exception)
        }



    }
    fun unlikedSsackbar(negocio: Negocio, likeButton: LikeButton, position: Int){
        // Elimina el negocio de la lista y notifica al adaptador sobre el cambio
        //val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            (negocios as MutableList<Negocio>).remove(negocio)
            notifyItemRemoved(position)
        }
        val snackbar = Snackbar.make(likeButton, "Negocio eliminado", Snackbar.LENGTH_LONG)
        snackbar.setAction("Deshacer") {
            (negocios as MutableList<Negocio>).add(position, negocio)
            notifyItemInserted(position)
        }
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    // El Snackbar se ha descartado sin seleccionar "Deshacer", por lo tanto, eliminamos el documento
                    val alias = negocio.alias
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val query = offersCollection.whereEqualTo("id_user", userId).whereEqualTo("alias", alias)
                    query.get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            document.reference.delete()
                                .addOnSuccessListener {
                                    Log.d(TAG, "Documento eliminado correctamente")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error al eliminar el documento", e)
                                }
                        }
                    }.addOnFailureListener { exception ->
                        Log.w(TAG, "Error al obtener el documento: ", exception)
                    }
                }
            }
        })
        snackbar.show()
    }

    fun showAlert(context: Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("ok") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    // Agrega el método removeNegocio() para eliminar un elemento de la lista
    fun removeNegocio(negocio: Negocio) {
        val position = negocios.indexOf(negocio)
        if (position != -1) {
            (negocios as MutableList<Negocio>).remove(negocio)
            notifyItemRemoved(position)
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