package com.fdez.projecttfg.adapters
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdez.projecttfg.api.OnItemClickListenerNegocio
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.SmallItemCardLocalesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.like.LikeButton
import com.like.OnLikeListener

class NegocioAdapterSmall(
    private val negocios: List<Negocio>,
    private var listener: OnItemClickListenerNegocio? = null,
    private val context: Context

) :
    RecyclerView.Adapter<NegocioAdapterSmall.NegocioViewHolder>() {
    private val db = Firebase.firestore
    private val offersCollection = db.collection("likes")

    inner class NegocioViewHolder(private val binding: SmallItemCardLocalesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(negocio: Negocio) {
            Glide.with(binding.root.context)
                .load(negocio.image_url)
                .error(R.drawable.iconimagenotfoundfree)
                .into(binding.imgNegocioS)
            binding.tvNombreS.text = negocio.name
            //binding.tvLugarS.text = negocio.coordinates?.city
            if (negocio.is_closed) {
                //binding.ivIsClosed.setImageResource(R.drawable.closed);
            } else {
                //binding.ivIsClosed.setImageResource(R.drawable.open);
            }
            binding.ratingBarS.rating = negocio.rating.toFloat();

            binding.root.setOnClickListener {
                listener?.onItemClick(negocio)
            }
            binding.likeButton.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {

                    //Guarda el alias del usuario en la base de datos
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val alias = negocio.alias
                    if(userId != null ) {
                        val offer = hashMapOf(
                            "id_user" to userId.toString(),
                            "alias" to alias,
                            "liked" to "true"
                        )
                        offersCollection.add(offer)
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "Documento agregado con ID: \${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error al agregar el documento", e)
                            }
                    }else{
                        binding.likeButton.isLiked =
                            false
                        //Toast.makeText(context, "Regístrate para tener acceso a todas las funcionalidades.", Toast.LENGTH_SHORT).show()
                        showAlert(context, "","Regístrate para tener acceso a todas las funcionalidades.")
                    }



                }
                override fun unLiked(likeButton: LikeButton) {
                    // Elimina el documento de la colección "likes" que corresponde a este usuario y a este negocio
                    val alias = negocio.alias
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val query = offersCollection.whereEqualTo("id_user", userId).whereEqualTo("alias", alias)
                    query.get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            document.reference.delete()
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Documento eliminado correctamente")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error al eliminar el documento", e)
                                }
                        }
                    }.addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error al obtener el documento: ", exception)
                    }



                }
            })
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val alias = negocio.alias

            val query = offersCollection
                .whereEqualTo("id_user", userId.toString())
                .whereEqualTo("alias", alias)

            query.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val liked = document.getString("liked")

                    binding.likeButton.isLiked =
                        liked != null && liked == "true" && alias == negocio.alias //Actualiza el estado del botón de "me gusta" en la pantalla
                }
            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error al obtener el documento: ", exception)
            }
        }

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

    fun setOnItemClickListener(listener: OnItemClickListenerNegocio) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NegocioViewHolder {
        val binding =
            SmallItemCardLocalesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NegocioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NegocioViewHolder, position: Int) {
        holder.bind(negocios[position])
    }

    override fun getItemCount(): Int = negocios.size


}