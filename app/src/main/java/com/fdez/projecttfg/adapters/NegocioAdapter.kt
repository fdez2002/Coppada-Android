package com.fdez.projecttfg.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdez.projecttfg.Api.OnItemClickListenerNegocio
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.R
import com.fdez.projecttfg.databinding.ItemCardLocalesBinding

class NegocioAdapter(
    private val negocios: List<Negocio>,
    private var listener: OnItemClickListenerNegocio? = null
) :
    RecyclerView.Adapter<NegocioAdapter.NegocioViewHolder>() {

    inner class NegocioViewHolder(private val binding: ItemCardLocalesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(negocio: Negocio) {
            Glide.with(binding.root.context)
                .load(negocio.image_url)
                .error(com.denzcoskun.imageslider.R.drawable.error)
                .into(binding.imgNegocio)
            binding.tvNombre.text = negocio.name
            binding.tvLugar.text = negocio.coordinates!!.city
            if (negocio.is_closed) {
                //binding.ivIsClosed.setImageResource(R.drawable.closed);
            } else {
                //binding.ivIsClosed.setImageResource(R.drawable.open);
            }
            binding.ratingBar.rating = negocio.rating.toFloat();

            binding.root.setOnClickListener {
                listener?.onItemClick(negocio)
            }
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