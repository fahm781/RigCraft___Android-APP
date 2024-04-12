package com.fahm781.rigcraft.savedBuildPackage

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

    @OptIn(DelicateCoroutinesApi::class)
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


            // Find the buttons in the dialog
            val shareBuildButton: Button = dialog.findViewById(R.id.shareBuildButton)
            val closeButton: Button = dialog.findViewById(R.id.closeButton)


            shareBuildButton.setOnClickListener {
                    // Convert the productItems list to a HashMap
                    val buildDataMap = hashMapOf<String, Any>()
                    for (productItem in productItems) {
                        buildDataMap[productItem.type] = productItem.details
                    }


                    // Create the main HashMap that is being saved to Firestore
                    val buildData = hashMapOf<String, Any>()
                    buildData["buildData"] = buildDataMap
//                    buildData["buildName"] = buildName
//                    buildData["comment"] = comment

                    val userEmail = FirebaseAuth.getInstance().currentUser?.email
                    if (userEmail != null) {
                        buildData["userEmail"] = userEmail
                    }

                    // Generate a unique identifier for the build (like a hash of the build data) and add it to the build data
                    val buildIdentifier = buildData.hashCode().toString()
                    buildData["buildIdentifier"] = buildIdentifier

                    // Save the build to Firestore
                    GlobalScope.launch(Dispatchers.IO) {
                        val db = FirebaseFirestore.getInstance()
                        // Check if the build already exists in the "SharedBuilds" collection using the unique buildIdentifier identifier
                        val existingBuild = db.collection("SharedBuilds")
                            .whereEqualTo("buildIdentifier", buildIdentifier).get()
                            .await().documents
                        if (existingBuild.isNotEmpty()) {
                            // If the build already exists, show a toast message and return
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    it.context,
                                    "This build has already been shared",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@launch
                        }

                        // If the build does not exist in the database, show the dialog
                        withContext(Dispatchers.Main) {
                            showAlertDialog(it) { (buildName, comment) ->
                                buildData["buildName"] = buildName
                                buildData["comment"] = comment

                                val newBuildId = db.collection("SharedBuilds").document().id
                                db.collection("SharedBuilds").document(newBuildId).set(buildData)
                                    .addOnSuccessListener {
                                        dialog.dismiss()
                                    }.addOnFailureListener { e ->
                                        Log.w("Firestore", "Error sharing build", e)
                                    }
                            }
                        }
                    }
            }
            closeButton.setOnClickListener {
                dialog.dismiss()
            }

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

    private fun showAlertDialog(view: View, callback: (Pair<String, String>) -> Unit) {
        val builder = AlertDialog.Builder(view.context)
        val inflater = LayoutInflater.from(view.context)
        val dialogView = inflater.inflate(R.layout.dialog_share_build, null)

        val buildNameEditText: EditText = dialogView.findViewById(R.id.buildNameEditText)
        val commentEditText: EditText = dialogView.findViewById(R.id.commentEditText)

        builder.setView(dialogView)
        builder.setPositiveButton("Submit") { dialog, _ ->
            val buildName = buildNameEditText.text.toString()
            val comment = commentEditText.text.toString()
            callback(Pair(buildName, comment))
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    fun updateData(newSavedBuilds: MutableList<SavedBuild>) {
        this.savedBuilds = newSavedBuilds
    }

    }
