package com.fahm781.rigcraft

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.fragment.findNavController
import com.fahm781.rigcraft.EbayServices.EbayTokenRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
               Bundle().apply {putString("productType", "cpu")})
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
          //  EbayTokenRepo().getToken()
        }


    }






    fun saveBuild(){
        //save the selected parts to the database
//        val msg = hashMapOf(
//            //empty hashmap for now
//        )
//
//        db.collection("Builds")
//            .add(msg)
//            .addOnSuccessListener { documentReference ->
//                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.w("Firestore", "Error adding document", e)
//            }
    }

    fun deletebuild(){



    }

}