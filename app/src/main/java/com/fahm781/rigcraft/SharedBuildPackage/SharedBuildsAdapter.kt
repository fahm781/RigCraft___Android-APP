package com.fahm781.rigcraft.SharedBuildPackage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.R
import com.squareup.picasso.Picasso

class SharedBuildsAdapter(var sharedBuilds: MutableList<SharedBuild>) : RecyclerView.Adapter<SharedBuildsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buildName: TextView = view.findViewById(R.id.buildName)
        val sharedBy: TextView = view.findViewById(R.id.sharedBy)
        val productLayout: LinearLayout = view.findViewById(R.id.productLayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_shared_build_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = sharedBuilds.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sharedBuild = sharedBuilds[position]
        holder.buildName.text = "Build Name: " + sharedBuild.buildIdentifier //sets the build name to the buildIdentifier for now
        holder.sharedBy.text = "Shared by: " + sharedBuild.userEmail

        // Add a TextView for each product in the build to the productLayout
        for ((productType, productDetailsMap) in sharedBuild.buildData) {
            val productDetails = productDetailsMap as Map<String, Any>

            val productItemView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.sharedproduct_item_layout, holder.productLayout, false)
            val prodType : TextView = productItemView.findViewById(R.id.productType)
            val titleTextView: TextView = productItemView.findViewById(R.id.titleTextView)
            val priceTextView: TextView = productItemView.findViewById(R.id.priceTextView)
            val productImageView: ImageView = productItemView.findViewById(R.id.productImageView)

            prodType.text = productType
            titleTextView.text = productDetails["title"] as String
            priceTextView.text = "£"+ productDetails["price"] as String
            Picasso.get().load(productDetails["imageUrl"] as String).into(productImageView)

            holder.productLayout.addView(productItemView)
        }
        Log.d("SharedBuildsAdapter", "onBindViewHolder: " + sharedBuild)
    }
}