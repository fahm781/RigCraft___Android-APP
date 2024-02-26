package com.fahm781.rigcraft

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.fahm781.rigcraft.EbayServices.EbayTokenRegenerator

import com.fahm781.rigcraft.EbayServices.RetrofitClient
import com.fahm781.rigcraft.EbayServices.SearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var moreDetailsButton: Button
    private lateinit var heading: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_product_list, container, false)
        moreDetailsButton = view.findViewById(R.id.moreDetailsButton)
        moreDetailsButton.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_productPageFragment)
        }

        var productType = requireArguments().getString("productType")

        if (productType != null) {
            heading = view.findViewById(R.id.heading)
            heading.text = "Select " + productType + ":"
        }

        searchEbayForItems(productType.toString())
        return view
    }


//    fun searchEbayForItems(query: String) {
//        RetrofitClient.ebayApi.searchItems(query).enqueue(object : Callback<SearchResult> {
//            override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
//                if (response.isSuccessful) {
//                    response.body()?.let { searchResult ->
//                        for (item in searchResult.itemSummaries) {
//                            Log.d("EbaySearch", "Item: ${item.title}")
//                        }
//                    }
//                } else {
//                    Log.e("EbaySearch", "Search failed: ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
//                Log.e("EbaySearch", "Network error: ${t.message}")
//            }
//        })
//    }

    fun searchEbayForItems(query: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val token = EbayTokenRegenerator().getToken()
            if (token != null) {
                RetrofitClient.ebayApi.searchItems("Bearer $token", query).enqueue(object : Callback<SearchResult> {
                    override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                        if (response.isSuccessful) {
                            response.body()?.let { searchResult ->
                                for (item in searchResult.itemSummaries) {
                                    Log.d("EbaySearch", "Item: ${item.title}" + "// Price: ${item.price.value}" + "// Price Currency: ${item.price.currency}" + "// Item URL: ${item.itemWebUrl}" + "// Image URL: ${item.image.imageUrl}")
                                }
                            }
                        } else {
                            Log.e("EbaySearch", "Search failed: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                        Log.e("EbaySearch", "Network error: ${t.message}")
                    }
                })
            }
        }
    }

}