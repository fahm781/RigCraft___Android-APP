package com.fahm781.rigcraft

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductPageFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var titleTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var itemWebUrlButton: Button
    private lateinit var backButton: Button


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
        val view = inflater.inflate(R.layout.fragment_product_page, container, false)

        val title = arguments?.getString("title")
        val price = arguments?.getString("price")
        val imageUrl = arguments?.getString("imageUrl")
        val itemWebUrl = arguments?.getString("itemWebUrl")
        titleTextView = view.findViewById(R.id.titleTextView)
        priceTextView = view.findViewById(R.id.priceTextView)
        imageView = view.findViewById(R.id.itemImageView)
        itemWebUrlButton = view.findViewById(R.id.lookOnEbayButton)

        titleTextView.text = title
        priceTextView.text = "£" +price
        Picasso.get().load(imageUrl).into(imageView)

        itemWebUrlButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itemWebUrl))
            startActivity(intent)
        }

        backButton = view.findViewById(R.id.backButton)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductPageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}