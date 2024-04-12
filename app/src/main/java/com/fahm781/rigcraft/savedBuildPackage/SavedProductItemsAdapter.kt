package com.fahm781.rigcraft.savedBuildPackage

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.R
import com.squareup.picasso.Picasso

class SavedProductItemsAdapter (private val productItems: List<SavedProductItem>) : RecyclerView.Adapter<SavedProductItemsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productType: TextView = view.findViewById(R.id.productType)
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val productLinkButton: Button = view.findViewById(R.id.productLinkButton)
        val productTitle: TextView = view.findViewById(R.id.productTitle)
        //can add more Items if needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_saved_build_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = productItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productItem = productItems[position]
        holder.productType.text = productItem.type
        val details = productItem.details as? Map<String, Any>
        if (details != null) {
            // Access each element using its key
            holder.productPrice.text = details["price"].toString()
            holder.productTitle.text = details["title"].toString()
            Picasso.get().load(details["imageUrl"].toString()).into(holder.productImage)
            val itemWebUrl = details["itemWebUrl"]
            holder.productLinkButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itemWebUrl.toString()))
                holder.itemView.context.startActivity(intent)
            }
        }

        }


}