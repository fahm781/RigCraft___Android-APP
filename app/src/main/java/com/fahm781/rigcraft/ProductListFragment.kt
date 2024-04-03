package com.fahm781.rigcraft

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.ebayServices.EbayTokenRegenerator
import com.fahm781.rigcraft.ebayServices.ItemSummary

import com.fahm781.rigcraft.ebayServices.EbayApiClient
import com.fahm781.rigcraft.ebayServices.SearchResult
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
//    private lateinit var moreDetailsButton: Button
    private lateinit var heading: TextView
    private lateinit var productrecyclerView: RecyclerView
    //check if the product type is null
    private var productType: String? = null
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private var itemSummaries: List<ItemSummary> = ArrayList<ItemSummary>()
    private lateinit var progressBar: ProgressBar


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
        val view =  inflater.inflate(R.layout.fragment_product_list, container, false)

        //set the heading to the product type
        productrecyclerView = view.findViewById(R.id.productrecyclerView)
        productType = requireArguments().getString("productType")
        if (productType != null) {
            heading = view.findViewById(R.id.heading)
            heading.text = "Select " + productType + ":"
        }

        progressBar = view.findViewById(R.id.progressBar)
        //Show progress bar while loading
        progressBar.visibility = View.VISIBLE

        //search for the productType on ebay
        val categoryId = getCategoryID(productType.toString())
        searchEbayForItems(productType.toString(), categoryId)

        //filter out the search results based on the search query
        searchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
            filterSearchResults(newText.toString())
                return true
            }
        })

        return view
    }

    private fun searchEbayForItems(query: String, categoryId:String) {
        CoroutineScope(Dispatchers.Main).launch {
            val token = EbayTokenRegenerator().getToken()
            if (token != null) {
                EbayApiClient.ebayApi.searchItems("Bearer $token", query, categoryId, "conditionIds:{1000}").enqueue(object : Callback<SearchResult> {
                    override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                        progressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            response.body()?.let { searchResult ->
                                for (item in searchResult.itemSummaries) {
                                    itemSummaries = searchResult.itemSummaries
                                    productrecyclerView.adapter = ItemSummaryAdapter(searchResult.itemSummaries, productType.toString())
                                    productrecyclerView.layoutManager = LinearLayoutManager(context)
                                    Log.d("EbaySearch", "Search successful: $item")
                                }
                            }
                        } else {
                            Log.e("EbaySearch", "Search failed: ${response.errorBody()?.string()}")
                        }
                    }
                    override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        Log.e("EbaySearch", "Network error: ${t.message}")
                    }
                })
            }
        }
    }

    //gets the category id for the product type
    private fun getCategoryID(productType: String): String {
        return when (productType) {
//            "cpu" -> "164"
            "gpu" -> "27386&58058&175673"
//            "ram" -> "170083"
            "Pc storage" -> "175669"
//            "power supply" -> "1788"
//            "motherboard" -> "1244"
            else -> ""
        }
    }


    //filter the search results based on the search query
    fun filterSearchResults(searchQuery: String){
        val filteredList = ArrayList<ItemSummary>()
        for (item in itemSummaries){
            if (item.title.toLowerCase().contains(searchQuery.lowercase())){
                filteredList.add(item)
            }
        }
        productrecyclerView.adapter = ItemSummaryAdapter(filteredList, productType.toString())
        productrecyclerView.adapter?.notifyDataSetChanged()

    }

    }

