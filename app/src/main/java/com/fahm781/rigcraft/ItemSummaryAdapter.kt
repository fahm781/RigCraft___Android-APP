package com.fahm781.rigcraft


import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.EbayServices.ItemSummary
import com.squareup.picasso.Picasso

class ItemSummaryAdapter(private val itemSummaries: List<ItemSummary>) : RecyclerView.Adapter<ItemSummaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val priceTextView: TextView = view.findViewById(R.id.priceTextView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val itemWebUrlButton: Button = view.findViewById(R.id.itemWebUrlButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_summary_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemSummary = itemSummaries[position]
        holder.titleTextView.text = itemSummary.title
        holder.priceTextView.text = "${itemSummary.price.value} ${itemSummary.price.currency}"
        Picasso.get().load(itemSummary.image.imageUrl).into(holder.imageView)
        holder.itemWebUrlButton.setOnClickListener {
            //redirect user to the ebay listing page aka. the itemSuumary.itemWebUrl
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itemSummary.itemWebUrl))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = itemSummaries.size
}