package com.fahm781.rigcraft

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.SharedBuildPackage.SharedBuild
import com.fahm781.rigcraft.SharedBuildPackage.SharedBuildsAdapter
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SocialHubFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SocialHubFragment : Fragment() {
    private lateinit var sharedBuildsAdapter: SharedBuildsAdapter
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var param1 = it.getString(ARG_PARAM1)
            var param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_social_hub, container, false)
        // Initialize the RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.sharedBuildrecyclerView)
        sharedBuildsAdapter = SharedBuildsAdapter(mutableListOf())

        // Set the RecyclerView's layout manager and adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = sharedBuildsAdapter

        // Fetch the shared builds and update the adapter's data
        fetchSharedBuildsAndUpdateAdapter()



        return view
    }

    private fun fetchSharedBuildsAndUpdateAdapter() {
        val db = FirebaseFirestore.getInstance()
        // Fetch the shared builds from the Firestore collection
        db.collection("SharedBuilds")
            .get()
            .addOnSuccessListener { documents ->
                val sharedBuilds = mutableListOf<SharedBuild>()
                for (document in documents) {
//                    val sharedBuild = document.toObject(SharedBuild::class.java)
                    val buildIdentifier = document.getString("buildIdentifier") ?: ""
                    val userEmail = document.getString("userEmail") ?: ""
                    val buildData = document.get("buildData") as Map<String, Any>

                    val sharedBuild = SharedBuild(buildData, userEmail, buildIdentifier )
                    sharedBuilds.add(sharedBuild)
                    Log.d("fetchSharedBuilds", "buildData for $buildIdentifier: $buildData")

                }
                // Update the adapter's data and notify it of the changes
                sharedBuildsAdapter.sharedBuilds = sharedBuilds
                sharedBuildsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SocialHubFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SocialHubFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}