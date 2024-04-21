package com.fahm781.rigcraft.sharedBuildPackage

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class SharedBuildsAdapter(var sharedBuilds: MutableList<SharedBuild>) : RecyclerView.Adapter<SharedBuildsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buildName: TextView = view.findViewById(R.id.buildName)
        val sharedBy: TextView = view.findViewById(R.id.sharedBy)
        val commentTextView: TextView = view.findViewById(R.id.comment)
        val productLayout: LinearLayout = view.findViewById(R.id.productLayout)
        val likeCounter: TextView = view.findViewById(R.id.likeCounter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_shared_build_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = sharedBuilds.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sharedBuild = sharedBuilds[position]
        val likeButton: ToggleButton = holder.itemView.findViewById(R.id.likeButton)

        // Get the SharedPreferences instance
        val sharedPreferences = holder.itemView.context.getSharedPreferences("SharedBuildsPrefs", Context.MODE_PRIVATE)

        // Retrieve the state of the like button from SharedPreferences
        val isLiked = sharedPreferences.getBoolean(sharedBuild.buildIdentifier, false)
        likeButton.isChecked = isLiked

        holder.buildName.text = if (sharedBuild.buildName.isEmpty()) {
            "Build Name: " + sharedBuild.buildIdentifier
        } else {
            "Build Name: " + sharedBuild.buildName
        }
        holder.sharedBy.text = "Shared by: " + sharedBuild.userEmail
        holder.commentTextView.text = if (sharedBuild.comment.isEmpty()) {
            "Comment: -"
        } else {
            "Comment: " + sharedBuild.comment
        }

        addProductToLayout(sharedBuild, holder)

        val spinnerButton: Button = holder.itemView.findViewById(R.id.spinnerButton)
        spinnerButton.setOnClickListener {
            if (holder.productLayout.visibility == View.VISIBLE) {
                holder.productLayout.visibility = View.GONE
            } else {
                holder.productLayout.visibility = View.VISIBLE
            }
        }

        likeButton.setOnCheckedChangeListener { _, isChecked ->
            val db = FirebaseFirestore.getInstance()

            db.collection("SharedBuilds").whereEqualTo("buildIdentifier", sharedBuild.buildIdentifier)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Log.w("SharedBuildsAdapter", "No document found with buildIdentifier: ${sharedBuild.buildIdentifier}")
                    } else {
                        val docRef = documents.documents[0].reference

                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(docRef)
                            var likes = snapshot.getLong("likes") ?: 0L
                            likes = if (isChecked) {
                                likes + 1
                            } else {
                                likes - 1
                            }
                            transaction.update(docRef, "likes", likes)
                            likes
                        }.addOnSuccessListener { likes ->
                            holder.likeCounter.text = likes.toString()


                        }.addOnFailureListener { e ->
                            Log.w("SharedBuildsAdapter", "Transaction failure.", e)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("SharedBuildsAdapter", "Error getting documents: ", e)
                }

            // Save the state of the like button in SharedPreferences when it changes
            with(sharedPreferences.edit()) {
                putBoolean(sharedBuild.buildIdentifier, isChecked)
                apply()
            }
        }
    }

    // Function to add the products to the layout
    private fun addProductToLayout(
        sharedBuild: SharedBuild,
        holder: SharedBuildsAdapter.ViewHolder
    ) {
        // Iterate over each product in the build
        for ((productType, productDetailsMap) in sharedBuild.buildData) {
            val productDetails = productDetailsMap as Map<String, Any>

            // Inflate the layout for each product and find the views
            val productItemView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.sharedproduct_item_layout, holder.productLayout, false)
            val prodType: TextView = productItemView.findViewById(R.id.productType)
            val titleTextView: TextView = productItemView.findViewById(R.id.titleTextView)
            val priceTextView: TextView = productItemView.findViewById(R.id.priceTextView)
            val productImageView: ImageView = productItemView.findViewById(R.id.productImageView)

            // Set the text and image for each view
            prodType.text = productType
            titleTextView.text = productDetails["title"] as String
            priceTextView.text = "£" + productDetails["price"] as String
            holder.likeCounter.text = sharedBuild.likes.toString()
            Picasso.get().load(productDetails["imageUrl"] as String).into(productImageView)

            // Add the product layout to the productLayout
            holder.productLayout.addView(productItemView)
        }
    }
}
