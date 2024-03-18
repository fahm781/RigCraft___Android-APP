package com.fahm781.rigcraft.SharedBuildPackage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
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
        val productLayout: LinearLayout = view.findViewById(R.id.productLayout)
        val likeCounter: TextView = view.findViewById(R.id.likeCounter)

//        val spinner: Spinner = view.findViewById(R.id.spinner)

//        init {
//            itemView.setOnClickListener {
//                if (productLayout.visibility == View.GONE) {
//                    productLayout.visibility = View.VISIBLE
//                } else {
//                    productLayout.visibility = View.GONE
//                }
//            }
//        }
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

        holder.buildName.text =
            "Build Name: " + sharedBuild.buildIdentifier //sets the build name to the buildIdentifier for now
        holder.sharedBy.text = "Shared by: " + sharedBuild.userEmail

        addProductToLayout(sharedBuild, holder)

        val spinnerButton: Button = holder.itemView.findViewById(R.id.spinnerButton)
        spinnerButton.setOnClickListener {
            if (holder.productLayout.visibility == View.VISIBLE) {
                holder.productLayout.visibility = View.GONE
            } else {
                holder.productLayout.visibility = View.VISIBLE
            }
        }


//        likeButton.setOnCheckedChangeListener { _, isChecked ->
//            val db = FirebaseFirestore.getInstance()
//            val docRef = db.collection("SharedBuilds").document(sharedBuild.buildIdentifier)
//
//            db.runTransaction { transaction ->
//                val snapshot = transaction.get(docRef)
//                var likes = snapshot.getDouble("likes")?.toInt() ?: 0
//                likes = if (isChecked) {
//                    likes + 1
//                } else {
//                    likes - 1
//                }
//                transaction.update(docRef, "likes", likes)
//                null
//            }.addOnSuccessListener {
//                holder.likeCounter.text = sharedBuild.likes.toString()
//                Log.d("SharedBuildsAdapter", " updated.")
//            }.addOnFailureListener { e ->
//                Log.w("SharedBuildsAdapter", "Transaction failure.", e)
//            }
//        }
//
//        likeButton.setOnCheckedChangeListener { _, isChecked ->
//            val db = FirebaseFirestore.getInstance()
//
//            db.collection("SharedBuilds").whereEqualTo("buildIdentifier", sharedBuild.buildIdentifier)
//                .get()
//                .addOnSuccessListener { documents ->
//                    if (documents.isEmpty) {
//                        Log.w("SharedBuildsAdapter", "No document found with buildIdentifier: ${sharedBuild.buildIdentifier}")
//                    } else {
//                        val docRef = documents.documents[0].reference
//
//                        db.runTransaction { transaction ->
//                            val snapshot = transaction.get(docRef)
//                            var likes = snapshot.getLong("likes") ?: 0L
//                            likes = if (isChecked) {
//                                likes + 1
//
//                            } else {
//                                likes - 1
//                            }
//                            transaction.update(docRef, "likes", likes)
//                            null
//                        }.addOnSuccessListener {
//                            holder.likeCounter.text = sharedBuild.likes.toString()
//                        }.addOnFailureListener { e ->
//                            Log.w("SharedBuildsAdapter", "Transaction failure.", e)
//                        }
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.w("SharedBuildsAdapter", "Error getting documents: ", e)
//                }
//        }
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
        }



//        holder.spinner.setOnClickListener {
//            if (holder.productLayout.visibility == View.VISIBLE) {
//                holder.productLayout.visibility = View.GONE
//            } else {
//                holder.productLayout.visibility = View.VISIBLE
//            }
//        }
//
//        holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                holder.productLayout.visibility = View.GONE
//            }
//
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                holder.productLayout.visibility = View.GONE
//            }
//        }
    }

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
//            val likeCounter: TextView = holder.itemView.findViewById(R.id.likeCounter)
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

//    fun getLikesFromDatabase(buildIdentifier: String) {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("SharedBuilds").whereEqualTo("buildIdentifier", buildIdentifier).get()
//        .addOnSuccessListener { querySnapshot ->
//                if (!querySnapshot.isEmpty) {
//                    val document = querySnapshot.documents[0]
//                    val likes = document.data?.get("likes") as String
//
//                    Log.d("SharedBuildsAdapter", "Likes: $likes")
//                } else {
//                    Log.d("SharedBuildsAdapter", "No such document")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d("SharedBuildsAdapter", "get failed with ", exception)
//            }
//    }
}
