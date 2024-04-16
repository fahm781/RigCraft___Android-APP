package com.fahm781.rigcraft

import android.app.AlertDialog
import android.content.Context
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
import com.fahm781.rigcraft.chatbotServices.ChatbotRepository
import com.fahm781.rigcraft.savedBuildPackage.SavedBuild
import com.fahm781.rigcraft.savedBuildPackage.SavedBuildsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random


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
    private lateinit var subtotalTextView: TextView
    private var savedBuildsAdapter: SavedBuildsAdapter? = null

    //    private lateinit var subtotal: TextView
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
                Bundle().apply { putString("productType",  "cpu") })
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

        subtotalTextView = view.findViewById(R.id.subtotal)

        val savedBuildsRecyclerView: RecyclerView = view.findViewById(R.id.savedBuildsRecyclerView)
        savedBuildsRecyclerView.layoutManager = LinearLayoutManager(context)
        getSavedBuilds { savedBuilds ->
            savedBuildsAdapter = SavedBuildsAdapter(savedBuilds)
            savedBuildsRecyclerView.adapter = savedBuildsAdapter
        }

        compatibilityCheck = view.findViewById(R.id.compatibilityCheck)
        compatibilityCheck.setOnClickListener {

            val buildDetails = getCurrentBuildDetails()
            if (buildDetails.isEmpty()) {
                Toast.makeText(context, "Please select a build", Toast.LENGTH_SHORT).show()
                Log.d("BuildDetails", "build details is empty")
                return@setOnClickListener
            }

            //If theres only one Item selected, return a toast message (therws nothing else to compare with)
            val count = getVisibleLayoutsCount()
            if (count < 2) {
                Toast.makeText(
                    context,
                    "You need to select more than 1 product",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            Log.d("BuildDetails Actual", buildDetails)
            // Generate a random integer between 1 and 100
            val randomNumber = Random.nextInt(1, 101)

            // Show the disclaimer if the user is new or at random (20% chance)
            if (isFirstTimeUser() || randomNumber <= 20) {
                showDisclaimer()
                setNotFirstTimeUser()
            }

            val chatbotRepository = ChatbotRepository()
            val prompt = "Check the compatibility of the components. And respond with GREEN if the components are compatible, RED if they are not compatible, and GREY if you are not sure. Ignore the price and other useless information"

            chatbotRepository.getResponse(buildDetails, prompt) { response ->
                Log.d("ChatbotResponse", response)
                when (response) {
                    "GREEN" -> setButtonStyle(compatibilityCheck,
                        ContextCompat.getColor(requireContext(), R.color.green),
                        "Compatible",
                        R.drawable.reshot_icon_tick_circle_nc7gmqhp6x
                    )
                    "RED" -> setButtonStyle(
                        compatibilityCheck,
                        ContextCompat.getColor(requireContext(), R.color.red),
                        "Incompatible",
                        R.drawable.red_x_icon
                    )
                    else -> setButtonStyle(
                        compatibilityCheck,
                        ContextCompat.getColor(requireContext(), R.color.grey),
                        "Compibility: Unverified",
                        R.drawable.question_mark_icon
                    )
                }
            }

        }
    }

    //this method is used to get the current build details as a string to pass to the chatbot for compatibility check
    private fun getCurrentBuildDetails(): String {
        val buildDetails = StringBuilder()
        for (productType in productTypes) {
            val selectedLayoutId =
                resources.getIdentifier("${productType}SelectedLayout", "id", activity?.packageName)
            val selectedLayout: LinearLayout = view?.findViewById(selectedLayoutId) as LinearLayout
            if (selectedLayout.visibility == View.VISIBLE) {
                val selectedTextViewId =
                    resources.getIdentifier("${productType}TextView", "id", activity?.packageName)
                val selectedTextView: TextView = view?.findViewById(selectedTextViewId) as TextView
                buildDetails.append("{" + productType + ": " + selectedTextView.text.toString())
                    .append("}")
            }
        }
        return buildDetails.toString()
    }

    //get the number of visible layouts (Items in the selected build list)
    private fun getVisibleLayoutsCount(): Int {
        var count = 0
        for (productType in productTypes) {
            val selectedLayoutId =
                resources.getIdentifier("${productType}SelectedLayout", "id", activity?.packageName)
            val selectedLayout: LinearLayout = view?.findViewById(selectedLayoutId) as LinearLayout
            if (selectedLayout.visibility == View.VISIBLE) {
                count++
            }
        }
        return count
    }

    //changing the compatibilityCheck button style based on the response from the chatbot
    private fun setButtonStyle(button: Button, color: Int, text: String, drawable: Int) {
        button.setBackgroundColor(color)
        button.text = text
        button.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
    }

    //set the compatibilityCheck button to default
    private fun setCompatibiltiyButtonToDefault() {
        setButtonStyle(compatibilityCheck,
            ContextCompat.getColor(requireContext(), R.color.compat_yellow),
            "PRESS TO CHECK FOR COMPATIBILITY",
            R.drawable.press)
    }

//    show the selected build items on the GUI
    private fun showSelectedBuild() {
        val db = FirebaseFirestore.getInstance()
        val userId = getUserID()
        setLayoutToGone()
        for (productType in productTypes) {
            val documentName = productType.replace("_", " ")
            if (userId != null) {
                db.collection("Users").document(userId).collection("currentBuild").document(documentName).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val title = document.data?.get("title").toString()
                            val image = document.data?.get("imageUrl").toString()
                            val price = document.data?.get("price").toString()

                            // Get the layout, ImageView, and TextView of the selected component
                            val selectedLayoutId = resources.getIdentifier(
                                "${productType}SelectedLayout",
                                "id",
                                activity?.packageName
                            )
                            val selectedLayout: LinearLayout =
                                view?.findViewById(selectedLayoutId) as LinearLayout
                            val selectedImageViewId = resources.getIdentifier(
                                "${productType}ImageView",
                                "id",
                                activity?.packageName
                            )
                            val selectedImageView: ImageView =
                                view?.findViewById(selectedImageViewId) as ImageView
                            val selectedTextViewId = resources.getIdentifier(
                                "${productType}TextView",
                                "id",
                                activity?.packageName
                            )
                            val selectedTextView: TextView =
                                view?.findViewById(selectedTextViewId) as TextView
                            val selectedPriceTextViewId = resources.getIdentifier(
                                "${productType}PriceTextView",
                                "id",
                                activity?.packageName
                            )
                            val selectedPriceTextView: TextView =
                                view?.findViewById(selectedPriceTextViewId) as TextView
                            val selectedRemoveButtonId = resources.getIdentifier(
                                "${productType}RemoveButton",
                                "id",
                                activity?.packageName
                            )
                            val selectedRemoveButton: ImageButton =
                                view?.findViewById(selectedRemoveButtonId) as ImageButton

                            // Set the visibility, image, title and price
                            selectedLayout.visibility = View.VISIBLE
                            Picasso.get().load(image).into(selectedImageView)
                            selectedTextView.text = title
                            selectedPriceTextView.text = "£ ${price}"

                            // delete the selected item from the build if the remove button is clicked
                            selectedRemoveButton.setOnClickListener {
                                deleteBuild(documentName)
                            }
                            getCurrentBuildSubtotal()
                            Log.d("Firestore", "DocumentSnapshot data: ${document.data}")
                        }
                    }


                    .addOnFailureListener { exception ->
                        Log.d("Firestore", "get failed with ", exception)
                        Toast.makeText(
                            context,
                            "An Unknown Database Error Has Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    // Initially set all selected buildItem layout(s) to GONE
    private fun setLayoutToGone() {
        for (productType in productTypes) {
            val selectedLayoutId =
                resources.getIdentifier("${productType}SelectedLayout", "id", activity?.packageName)
            val selectedLayout: LinearLayout = view?.findViewById(selectedLayoutId) as LinearLayout
            selectedLayout.visibility = View.GONE
        }
    }

    //save the build to firestore database
    private fun saveBuild() {
        if (getCurrentBuildDetails().isEmpty()) {
            Toast.makeText(context, "No items to save", Toast.LENGTH_SHORT).show()
            return
        }
        if (getVisibleLayoutsCount() < 2) {
            Toast.makeText(context, "You need to select more than 1 product", Toast.LENGTH_SHORT)
                .show()
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val userId = getUserID()
            if (userId != null) {
                val buildData = hashMapOf<String, Any>()
                for (productType in productTypes) {
                    val documentName = productType.replace("_", " ")
                    //need to use await to get the data from firestore before adding to buildData.
                    val document =
                        db.collection("Users").document(userId).collection("currentBuild")
                            .document(documentName).get().await()
                    val data = document.data
                    if (data != null) {
                        buildData[documentName] = data
                    }
                }
                val newBuildId = db.collection("Users").document(userId).collection("savedBuilds").document().id
                db.collection("Users").document(userId).collection("savedBuilds").document(newBuildId).set(buildData).addOnSuccessListener{
                        Log.d("Firestore", "Build successfully saved with ID: $newBuildId")
                        getSavedBuilds { savedBuilds ->
                            savedBuildsAdapter?.updateData(savedBuilds)
                            savedBuildsAdapter?.notifyDataSetChanged()
                        }
                        clearCurrentBuild()
                        subtotalTextView.visibility = View.GONE
                        setCompatibiltiyButtonToDefault()
                        Toast.makeText(context, "Build saved", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                    Log.w("Firestore", "Error saving build", e)
                    Toast.makeText(context, "Error saving Build", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //delete current build
    private fun deleteBuild(documentName: String) {
        val userId = getUserID()
        val db = FirebaseFirestore.getInstance()
        if (userId != null) {
            db.collection("Users").document(userId).collection("currentBuild").document(documentName).delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "DocumentSnapshot successfully deleted!")
                    showSelectedBuild() // Refresh the selected build
                    setCompatibiltiyButtonToDefault()
                    if (getVisibleLayoutsCount() == 0){
                        subtotalTextView.visibility = View.GONE
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error deleting document", e)
                    Toast.makeText(context, "Error deleting item from build", Toast.LENGTH_SHORT).show()
                }
        }
    }

    //clear current build
    private fun clearCurrentBuild() {
        if (getCurrentBuildDetails().isEmpty()) {
            Toast.makeText(context, "No items to clear", Toast.LENGTH_SHORT).show()
            return
        }
        //you have to delete one by one, firestore does not support batch delete hence the loop
        val db = FirebaseFirestore.getInstance()
        val userId = getUserID()
        if (userId != null) {
        for (productType in productTypes) {
            val documentName = productType.replace("_", " ")
            db.collection("Users").document(userId).collection("currentBuild").document(documentName).delete()
                    .addOnSuccessListener { documentReference ->
                        Log.d("Firestore", "Build Cleared")
                        showSelectedBuild()
                        setCompatibiltiyButtonToDefault()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error adding document", e)
                    }
            }
        }
        subtotalTextView.visibility = View.GONE
    }

    //get saved builds from firestore database, add them to the SavedBuild list and return it
    private fun getSavedBuilds(callback: (MutableList<SavedBuild>) -> Unit) {
        val savedBuilds = mutableListOf<SavedBuild>()
        val userId = getUserID()
        val db = FirebaseFirestore.getInstance()
        if (userId != null) {
            db.collection("Users").document(userId).collection("savedBuilds").get()
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

                    val savedBuildsHeading: TextView? = view?.findViewById(R.id.savedBuildsHeading) as? TextView
                    if (savedBuildsHeading != null) {
                        if (savedBuilds.size > 0) {
                            savedBuildsHeading.visibility = View.VISIBLE
                        } else {
                            savedBuildsHeading.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error fetching saved builds", exception)
                    Toast.makeText(context, "Error fetching saved builds", Toast.LENGTH_SHORT).show()
                }
        }
    }

    //Return the current user's ID
    private fun getUserID(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }

    //get the current build subtotal and display it on the partPickerFragment
    private fun getCurrentBuildSubtotal(){
        if (getVisibleLayoutsCount() > 0){
            var subtotal = 0.0
            for (productType in productTypes) {
                val selectedLayoutId = resources.getIdentifier("${productType}SelectedLayout", "id", activity?.packageName)
                val selectedLayout: LinearLayout = view?.findViewById(selectedLayoutId) as LinearLayout
                if (selectedLayout.visibility == View.VISIBLE) {
                    val selectedPriceTextViewId = resources.getIdentifier("${productType}PriceTextView", "id", activity?.packageName)
                    val selectedPriceTextView: TextView = view?.findViewById(selectedPriceTextViewId) as TextView
                    val priceText = selectedPriceTextView.text.toString()
                    val price = priceText.replace("£", "").trim().toDouble()
                    subtotal += price
                }
            }
            Log.d("SUBTOTAL", subtotal.toString())
            subtotalTextView.visibility = View.VISIBLE
            val formattedSubtotal = String.format("%.2f", subtotal) // Format the subtotal to 2 decimal places
            subtotalTextView.text = "Subtotal: £$formattedSubtotal"
        }
    }

    // store a flag indicating whether the user is new or not
    private fun isFirstTimeUser(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("PartPickerPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("firstTimeUser", true)
    }

    // set the flag to indicate that the user is not new
    private fun setNotFirstTimeUser() {
        val sharedPreferences = requireActivity().getSharedPreferences("PartPickerPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("firstTimeUser", false).apply()
    }

    // show a disclaimer to the user stating that the compatibility check may not be accurate
    private fun showDisclaimer() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Disclaimer")
        builder.setMessage("Please note that some compatibility checks may yield inaccurate results. Please verify the compatibility of the components before purchasing them.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}