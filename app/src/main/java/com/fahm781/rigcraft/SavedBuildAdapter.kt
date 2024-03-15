package com.fahm781.rigcraft

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class SavedBuildsAdapter(private var savedBuilds: MutableList<SavedBuild>) : RecyclerView.Adapter<SavedBuildsAdapter.ViewHolder>() {



    // Step 3: Define a ViewHolder
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buildNumber: TextView = view.findViewById(R.id.buildNumber)
        val viewBuildButton: Button = view.findViewById(R.id.viewBuildButton)
        val deleteBuildButton: Button = view.findViewById(R.id.deleteBuildButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_saved_build, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount()= savedBuilds.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val savedBuild = savedBuilds[position]
        holder.buildNumber.text = "Build No. "+ (position + 1).toString()
        holder.viewBuildButton.setOnClickListener {
            val dialog = Dialog(it.context)
            dialog.setContentView(R.layout.dialog_view_build)
            dialog.setCancelable(true) // The dialog will be dismissed when the user touches outside it

            val recyclerView: RecyclerView = dialog.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(it.context)

            // Create a list to hold the product items
                val productItems = mutableListOf<SavedProductItem>()
                for ((key, value) in savedBuild.buildData) {
                    // Create a ProductItem for each product and add it to the list
                    Log.d("SavedBuildsAdapterTESTTTT", "Key: $key, Value: $value")
                    val productItem = SavedProductItem(key, value)
                    productItems.add(productItem)
            }
            // Create an adapter for the RecyclerView and set it
                val adapter = SavedProductItemsAdapter(productItems)
                recyclerView.adapter = adapter

                dialog.show()
            }
            holder.deleteBuildButton.setOnClickListener{
                deleteBuildFromFirestore(position, holder.itemView.context)
            }

        }

    //delete the saved Build from Firestore
    private fun deleteBuildFromFirestore (position: Int, context: Context) {
        val buildId = savedBuilds[position].id
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("Users").document(userId).collection("savedBuilds").document(buildId)
                .delete()
                .addOnSuccessListener {
                    savedBuilds.removeAt(position)
                    notifyItemRemoved(position)
                    Log.d("Firestore", "Successfully deleted build with ID: $buildId")

                    // Update the visibility of the heading based on the item count
                    val savedBuildsHeading: TextView = (context as Activity).findViewById(R.id.savedBuildsHeading)
                    if (savedBuilds.size > 0) {
                        savedBuildsHeading.visibility = View.VISIBLE
                    } else {
                        savedBuildsHeading.visibility = View.GONE
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error deleting build", e)
                }
        }
    }

    fun updateData(newSavedBuilds: MutableList<SavedBuild>) {
        this.savedBuilds = newSavedBuilds
    }

    }
