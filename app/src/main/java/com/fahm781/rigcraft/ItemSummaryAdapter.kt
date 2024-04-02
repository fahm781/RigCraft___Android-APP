package com.fahm781.rigcraft


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.ebayServices.ItemSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ItemSummaryAdapter(private val itemSummaries: List<ItemSummary>,  private val productType: String) : RecyclerView.Adapter<ItemSummaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val priceTextView: TextView = view.findViewById(R.id.priceTextView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val itemWebUrlButton: Button = view.findViewById(R.id.itemWebUrlButton)
        val moreDetailsButton: Button = view.findViewById(R.id.moreDetailsButton)
        val addToBuildButton: Button = view.findViewById(R.id.addToBuildButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_summary_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemSummary = itemSummaries[position]
        holder.titleTextView.text = itemSummary.title
        holder.priceTextView.text = "£${itemSummary.price.value}"
        Picasso.get().load(itemSummary.image.imageUrl).into(holder.imageView)

        holder.itemWebUrlButton.setOnClickListener {
            //redirect user to the ebay listing page aka. the itemSuumary.itemWebUrl
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itemSummary.itemWebUrl))
            holder.itemView.context.startActivity(intent)
        }

        holder.moreDetailsButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", itemSummary.title)
            bundle.putString("price", itemSummary.price.value)
            bundle.putString("imageUrl", itemSummary.image.imageUrl)
            bundle.putString("itemWebUrl", itemSummary.itemWebUrl)
            holder.itemView.findNavController().navigate(R.id.action_productListFragment_to_productPageFragment, bundle)
        }

//        holder.addToBuildButton.setOnClickListener {
//            val user = FirebaseAuth.getInstance().currentUser
//            val userId = user?.uid
//            val db = FirebaseFirestore.getInstance()
//            val selectedBuild = hashMapOf(
//                "title" to itemSummary.title,
//                "price" to itemSummary.price.value,
//                "imageUrl" to itemSummary.image.imageUrl,
//                "itemWebUrl" to itemSummary.itemWebUrl
//                //can add more info later
//            )
//            db.collection("users").document(userId).collection("currentBuild").document(productType).set(selectedBuild)
//                .addOnSuccessListener {
//                    Log.d("Firestore", "DocumentSnapshot successfully written!")
//                    Toast.makeText(holder.itemView.context, "Item added to build", Toast.LENGTH_SHORT).show()
//                    holder.itemView.findNavController().navigate(R.id.action_productListFragment_to_partPickerFragment)
//                }
//                .addOnFailureListener { e ->
//                    Log.w("Firestore", "Error writing document", e)
//                    Toast.makeText(holder.itemView.context, "Error adding item to build", Toast.LENGTH_SHORT).show()
//                }
//        }

        holder.addToBuildButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid

            val db = FirebaseFirestore.getInstance()
            val selectedBuild = hashMapOf(
                "title" to itemSummary.title,
                "price" to itemSummary.price.value,
                "imageUrl" to itemSummary.image.imageUrl,
                "itemWebUrl" to itemSummary.itemWebUrl
                //can add more info later
            )
            if (userId != null) {
                db.collection("Users").document(userId).collection("currentBuild").document(productType).set(selectedBuild)
                    .addOnSuccessListener {
                        Log.d("Firestore", "DocumentSnapshot successfully written!")
                        Toast.makeText(holder.itemView.context, "Item added to build", Toast.LENGTH_SHORT).show()
                        holder.itemView.findNavController().navigate(R.id.action_productListFragment_to_partPickerFragment)
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error writing document", e)
                        Toast.makeText(holder.itemView.context, "Error adding item to build", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun getItemCount() = itemSummaries.size
}