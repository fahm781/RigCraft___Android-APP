package com.fahm781.rigcraft

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PartPickerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class PartPickerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var selectCpu: Button
    private lateinit var selectGpu: Button
    private lateinit var selectRam: Button
    private lateinit var selectStorage: Button
    private lateinit var selectPowersupply: Button
    private lateinit var selectMotherboard: Button
    private lateinit var saveBuild: Button
    private lateinit var clearBuild: Button
    private val db = FirebaseFirestore.getInstance()
//    val productTypes = listOf("cpu", "gpu", "ram", "storage", "powerSupply", "motherboard")
    val productTypes = listOf("cpu", "gpu", "ram", "Pc_storage", "power_supply", "motherboard")



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_partpicker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        selectCpu = view.findViewById(R.id.selectCpu)
        selectCpu.setOnClickListener {
            findNavController().navigate(R.id.action_partPickerFragment_to_productListFragment,
                Bundle().apply { putString("productType", "cpu") })
        }

        selectGpu = view.findViewById(R.id.selectGpu)
        selectGpu.setOnClickListener {
            findNavController().navigate(
                R.id.action_partPickerFragment_to_productListFragment,
                Bundle().apply { putString("productType", "gpu") })
        }

        //do the same as above for the rest of the buttons
        selectRam = view.findViewById(R.id.selectRam)
        selectRam.setOnClickListener {
            findNavController().navigate(
                R.id.action_partPickerFragment_to_productListFragment,
                Bundle().apply { putString("productType", "ram") })
        }
        selectStorage = view.findViewById(R.id.selectStorage)
        selectStorage.setOnClickListener {
            findNavController().navigate(
                R.id.action_partPickerFragment_to_productListFragment,
                Bundle().apply { putString("productType", "Pc storage") })
        }
        selectPowersupply = view.findViewById(R.id.selectPowersupply)
        selectPowersupply.setOnClickListener {
            findNavController().navigate(
                R.id.action_partPickerFragment_to_productListFragment,
                Bundle().apply { putString("productType", "power supply") })
        }

        selectMotherboard = view.findViewById(R.id.selectMotherboard)
        selectMotherboard.setOnClickListener {
            findNavController().navigate(
                R.id.action_partPickerFragment_to_productListFragment,
                Bundle().apply { putString("productType", "motherboard") })
        }

        saveBuild = view.findViewById(R.id.saveBuild)
        saveBuild.setOnClickListener {
            saveBuild()
        }

        clearBuild = view.findViewById(R.id.clearBuild)
        clearBuild.setOnClickListener {
            clearSelectedBuild()
        }

        showSelectedBuild()
    }

    suspend fun getApiKey():  String?  {
        return try {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("OpenAIAPIKey").document("api_key").get().await()
       // Log.d("Firestore", "Key is : ${snapshot.getString("key")}")
         snapshot.getString("key")
        } catch (e: Exception) {
            Log.d("Firestore", "Error getting documents: ", e)
            null
        }
    }

//    private fun showSelectedBuild() {
//        val db = FirebaseFirestore.getInstance()
//        for (productType in productTypes) {
//            val documentName =  productType.replace("_", " ")
//            db.collection("SelectedBuild").document(documentName).get()
//                .addOnSuccessListener { document ->
//                    if (document != null) {
//                        val selectedItemsLayout : LinearLayout? = view?.findViewById(R.id.selectedItemsLayout)
//                        selectedItemsLayout?.visibility = View.VISIBLE
//                        val title = document.data?.get("title").toString()
//                        val selectedTextViewId = resources.getIdentifier("${productType}SelectedTextView", "id", activity?.packageName)
//                        val selectedTextView: TextView = view?.findViewById(selectedTextViewId) as TextView
//                        selectedTextView.text = documentName + ": " + title + " - £" + document.data?.get("price").toString()
//                        val selectedItemsHeading: TextView = view?.findViewById(R.id.selectedItemsHeading) as TextView
//                        selectedItemsHeading.text = "Current Build:"
//                        selectedItemsHeading.visibility = View.VISIBLE
//                        Log.d("Firestore", "DocumentSnapshot data: ${document.data}")
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.d("Firestore", "get failed with ", exception)
//                }
//        }
//    }

    private fun showSelectedBuild() {
        val db = FirebaseFirestore.getInstance()
        var isEmpty = true
        val selectedItemsLayout : LinearLayout? = view?.findViewById(R.id.selectedItemsLayout)

        for (productType in productTypes) {
            val documentName =  productType.replace("_", " ")
            db.collection("SelectedBuild").document(documentName).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        isEmpty = false
                        selectedItemsLayout?.visibility = View.VISIBLE
                        val title = document.data?.get("title").toString()
                        val selectedTextViewId = resources.getIdentifier("${productType}SelectedTextView", "id", activity?.packageName)
                        val selectedTextView: TextView = view?.findViewById(selectedTextViewId) as TextView
                        selectedTextView.text = documentName + ": " + title + " - £" + document.data?.get("price").toString()
                        val selectedItemsHeading: TextView = view?.findViewById(R.id.selectedItemsHeading) as TextView
                        selectedItemsHeading.text = "Current Build:"
                        selectedItemsHeading.visibility = View.VISIBLE
                        Log.d("Firestore", "DocumentSnapshot data: ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "get failed with ", exception)
                }
        }

        if (isEmpty) {
            selectedItemsLayout?.visibility = View.GONE
        } else {
            selectedItemsLayout?.visibility = View.VISIBLE
        }
    }


     fun saveBuild() {
        GlobalScope.launch(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val buildData = hashMapOf<String, Any>()
            for (productType in productTypes) {
                val documentName = productType.replace("_", " ")
                //need to use await to get the data from firestore before adding to buildData.
                val document = db.collection("SelectedBuild").document(documentName).get().await()
                val data = document.data
                if (data != null) {
                    buildData[documentName] = data
                }
            }
            val newBuildId = db.collection("SavedBuilds").document().id
            db.collection("SavedBuilds").document(newBuildId).set(buildData).addOnSuccessListener {
                Log.d("Firestore", "Build successfully saved with ID: $newBuildId")
                clearSelectedBuild()
                Toast.makeText(context, "Build saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Log.w("Firestore", "Error saving build", e)
            }
        }
    }

    fun deletebuild(id: String){
        val db = FirebaseFirestore.getInstance()
        db.collection("SavedBuilds").document(id).delete()
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Build Deleted")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }



    fun clearSelectedBuild(){
        //you have to delete one by one, firestore does not support batch delete
        val db = FirebaseFirestore.getInstance()
        for (productType in productTypes) {
            val documentName =  productType.replace("_", " ")
            db.collection("SelectedBuild").document(documentName).delete()
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "Build Cleared")
                    showSelectedBuild()
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error adding document", e)
                }
        }

    }

//    fun showSavedBuilds(){
//        val db = FirebaseFirestore.getInstance()
//        db.collection("SavedBuilds").get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d("Firestore", "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("Firestore", "Error getting documents.", exception)
//            }
//    }

}