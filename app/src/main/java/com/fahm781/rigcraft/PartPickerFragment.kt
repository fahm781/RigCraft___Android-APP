package com.fahm781.rigcraft

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.ChatbotServices.ChatbotRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
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
    private lateinit var compatibilityCheck: Button
    val productTypes = listOf("cpu", "gpu", "ram", "Pc_storage", "power_supply", "motherboard")



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_partpicker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //select each component
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

        //save the build if any
        saveBuild = view.findViewById(R.id.saveBuild)
        saveBuild.setOnClickListener {
            saveBuild()
        }

        //clear selected build if any
        clearBuild = view.findViewById(R.id.clearBuild)
        clearBuild.setOnClickListener {
            clearCurrentBuild()
        }

        //show selected builds if any
        showSelectedBuild()

        //show saved builds if any
        val savedBuildsRecyclerView: RecyclerView = view.findViewById(R.id.savedBuildsRecyclerView)
        savedBuildsRecyclerView.layoutManager = LinearLayoutManager(context)
        getSavedBuilds { savedBuilds ->
            savedBuildsRecyclerView.adapter = SavedBuildsAdapter(savedBuilds)
        }


        compatibilityCheck = view.findViewById(R.id.compatibilityCheck)
        compatibilityCheck.setOnClickListener {
            val buildDetails = getCurrentBuildDetails()
            if (buildDetails.isEmpty()) {
                Toast.makeText(context, "Please select a build", Toast.LENGTH_SHORT).show()
                Log.d("BuildDetails", "build details is empty")
            }

            Log.d("BuildDetails Actual", buildDetails)
            val chatbotRepository = ChatbotRepository()
            val prompt = "Check the compatibility of the components. And respond with GREEN if the components are compatible, RED if they are not compatible, and GREY if you are not sure. Ignore the price and other useless information"

            chatbotRepository.getResponse(buildDetails, prompt) { response ->
                //return the message from the chatbot and display it in a Log message
                Log.d("ChatbotResponse", response)
                when (response) {
                    "GREEN" -> context?.let { it1 -> ContextCompat.getColor(it1, R.color.green) }
                        ?.let { it2 ->
                            setButtonStyle(compatibilityCheck,
                                it2, "Compatible", R.drawable.reshot_icon_tick_circle_nc7gmqhp6x, 105)
                        }
                    "RED" -> context?.let { it1 -> ContextCompat.getColor(it1, R.color.red) }
                        ?.let { it2 ->
                            setButtonStyle(compatibilityCheck,
                                it2, "Uncompatible", R.drawable.red_x_icon, 100)
                        }
                    else -> context?.let { it1 -> ContextCompat.getColor(it1, R.color.grey) }
                        ?.let { it2 ->
                            setButtonStyle(compatibilityCheck,
                                it2, "Compibility: Unverified", R.drawable.question_mark_icon, 70)
                        }
                }
            }
        }
    }


    //this method is used to get the current build details as a string to pass to the chatbot for compatibility check
    private fun getCurrentBuildDetails(): String {
        val buildDetails = StringBuilder()
        for (productType in productTypes) {
            val selectedLayoutId = resources.getIdentifier("${productType}SelectedLayout", "id", activity?.packageName)
            val selectedLayout: LinearLayout = view?.findViewById(selectedLayoutId) as LinearLayout
            if (selectedLayout.visibility == View.VISIBLE) {
                val selectedTextViewId = resources.getIdentifier("${productType}TextView", "id", activity?.packageName)
                val selectedTextView: TextView = view?.findViewById(selectedTextViewId) as TextView
                buildDetails.append("{"+ productType+ ": "+selectedTextView.text.toString()).append("}")
            }
        }
        return buildDetails.toString()
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

    fun setButtonStyle(button: Button, color: Int, text: String, drawable: Int, padding: Int) {
        button.setBackgroundColor(color)
        button.text = text
        button.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
        button.compoundDrawablePadding = padding
    }

//    private fun showSelectedBuild() {
//        val db = FirebaseFirestore.getInstance()
//        var isEmpty = true
//        val selectedItemsLayout : LinearLayout? = view?.findViewById(R.id.selectedItemsLayout)
//
//        for (productType in productTypes) {
//            val documentName =  productType.replace("_", " ")
//            db.collection("SelectedBuild").document(documentName).get()
//                .addOnSuccessListener { document ->
//                    if (document != null && document.exists()) {
//                        isEmpty = false
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
//
//        if (isEmpty) {
//            selectedItemsLayout?.visibility = View.GONE
//        } else {
//            selectedItemsLayout?.visibility = View.VISIBLE
//        }
//    }


    private fun showSelectedBuild() {
        val db = FirebaseFirestore.getInstance()
        setLayoutToGone()
        for (productType in productTypes) {
            val documentName =  productType.replace("_", " ")
            db.collection("SelectedBuild").document(documentName).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val title = document.data?.get("title").toString()
                        val image = document.data?.get("imageUrl").toString()
                        val price = document.data?.get("price").toString()

                        // Get the layout, ImageView, and TextView of the selected component
                        val selectedLayoutId = resources.getIdentifier("${productType}SelectedLayout", "id", activity?.packageName)
                        val selectedLayout: LinearLayout = view?.findViewById(selectedLayoutId) as LinearLayout
                        val selectedImageViewId = resources.getIdentifier("${productType}ImageView", "id", activity?.packageName)
                        val selectedImageView: ImageView = view?.findViewById(selectedImageViewId) as ImageView
                        val selectedTextViewId = resources.getIdentifier("${productType}TextView", "id", activity?.packageName)
                        val selectedTextView: TextView = view?.findViewById(selectedTextViewId) as TextView
                        val selectedRemoveButtonId = resources.getIdentifier("${productType}RemoveButton", "id", activity?.packageName)
                        val selectedRemoveButton: ImageButton = view?.findViewById(selectedRemoveButtonId) as ImageButton

                        // Set the visibility, image, title and price
                        selectedLayout.visibility = View.VISIBLE
                        Picasso.get().load(image).into(selectedImageView)
                        selectedTextView.text = title + " - £" + price

                        // delete the selected item from the build if the remove button is clicked
                        selectedRemoveButton.setOnClickListener {
                            deletebuild(documentName)
                        }
                        Log.d("Firestore", "DocumentSnapshot data: ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "get failed with ", exception)
                    Toast.makeText(context, "An Unknown Database Error Has Occurred", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Initially set all selected buildItem layout(s) to GONE
    private fun setLayoutToGone() {
        for (productType in productTypes) {
            val selectedLayoutId = resources.getIdentifier("${productType}SelectedLayout", "id", activity?.packageName)
            val selectedLayout: LinearLayout = view?.findViewById(selectedLayoutId) as LinearLayout
            selectedLayout.visibility = View.GONE
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
                clearCurrentBuild()
                Toast.makeText(context, "Build saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Log.w("Firestore", "Error saving build", e)
                Toast.makeText(context, "Error saving Build", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //delete current build
    fun deletebuild(documentName: String){
        val db = FirebaseFirestore.getInstance()
        db.collection("SelectedBuild").document(documentName).delete()
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot successfully deleted!")
                showSelectedBuild() // Refresh the selected build
//                                    selectedLayout.visibility = View.GONE // Hide the layout (temporary fix)
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting document", e)
                Toast.makeText(context, "Error deleting item from build", Toast.LENGTH_SHORT).show()
            }
    }

    //clear current build
    fun clearCurrentBuild(){
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

//private suspend fun fetchSavedBuilds(): List<SavedBuild> {
//    val savedBuilds = mutableListOf<SavedBuild>()
//    try {
//        val db = FirebaseFirestore.getInstance()
//        val snapshot = db.collection("SavedBuilds").get().await()
//        for (document in snapshot.documents) {
//            val id = document.id
//            val buildData = document.data
//            if (buildData != null) {
//                val savedBuild = SavedBuild(id, buildData)
//                savedBuilds.add(savedBuild)
//            }
//        }
//    } catch (e: Exception) {
//        Log.w("Firestore", "Error fetching saved builds", e)
//    }
//    return savedBuilds
//}


    //get saved builds from firestore database, add them to the SavedBuild list and return it
    private fun getSavedBuilds(callback: (List<SavedBuild>) -> Unit) {
        val savedBuilds = mutableListOf<SavedBuild>()
        val db = FirebaseFirestore.getInstance()
        db.collection("SavedBuilds").get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    val id = document.id
                    val buildData = document.data
                    if (buildData != null) {
                        val savedBuild = SavedBuild(id, buildData)
                        savedBuilds.add(savedBuild)
                    }
                }
                callback(savedBuilds)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error fetching saved builds", exception)
                Toast.makeText(context, "Error fetching saved builds", Toast.LENGTH_SHORT).show()
            }
    }

}