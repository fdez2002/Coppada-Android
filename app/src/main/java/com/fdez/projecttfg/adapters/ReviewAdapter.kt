package com.fdez.projecttfg.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdez.projecttfg.Api.OnItemClickListenerNegocio
import com.fdez.projecttfg.Review
import com.fdez.projecttfg.databinding.ItemCardLocalesBinding
import com.fdez.projecttfg.databinding.ItemCardReviwBinding

class ReviewAdapter (
    private val review: List<Review>,
) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    inner class ReviewViewHolder(private val binding: ItemCardReviwBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(review: Review){

                Glide.with(binding.root.context)
                    .load(review.user.image_url)
                    .error(com.denzcoskun.imageslider.R.drawable.error)
                    .into(binding.imageViewUser)
                binding.textViewNameUser.text = review.user.name
                binding.ratingBarUser.rating = review.rating.toFloat()
                binding.textViewRatingNumber.text = review.rating.toString()
                binding.textViewTextUser.text = review.text
            }
    }
        override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewAdapter.ReviewViewHolder {
            val binding =
                ItemCardReviwBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(review[position])
    }

    override fun getItemCount(): Int = review.size

}