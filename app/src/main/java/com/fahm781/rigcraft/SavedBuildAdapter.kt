package com.fahm781.rigcraft

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.coroutineContext

class SavedBuildsAdapter(private val savedBuilds: List<SavedBuild>) : RecyclerView.Adapter<SavedBuildsAdapter.ViewHolder>() {

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
        holder.buildNumber.text = savedBuild.id
        holder.viewBuildButton.setOnClickListener {
            // Handle view build button click
            val buildDataString = StringBuilder()
            for ((key, value) in savedBuild.buildData) {
                buildDataString.append("$key: $value\n\n")
            }

            // Create an AlertDialog to display the build data
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Build Details")
            builder.setMessage(buildDataString.toString())
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        holder.deleteBuildButton.setOnClickListener{
            deleteBuildFromFirestore(position)
        }
        }

    private fun deleteBuildFromFirestore (position: Int) {
        val buildId = savedBuilds[position].id
        val db = FirebaseFirestore.getInstance()
        db.collection("SavedBuilds").document(buildId)
            .delete()
            .addOnSuccessListener {
//                savedBuilds.toMutableList().removeAt(position)
//                notifyItemRemoved(position)
                Log.d("Firestore", "Successfully deleted build with ID: $buildId")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting build", e)
            }
    }

    }
