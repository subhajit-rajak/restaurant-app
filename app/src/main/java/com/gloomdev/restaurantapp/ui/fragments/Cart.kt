package com.gloomdev.restaurantapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.ui.adapter.CartAdapter
import com.gloomdev.restaurantapp.ui.dataclass.CartDetailes

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Cart.newInstance] factory method to
 * create an instance of this fragment.
 */
class Cart : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartAdapter
    private lateinit var ItemsTotal: TextView
    private lateinit var DeliveryService: TextView
    private lateinit var Tax: TextView
    private lateinit var TotalPrice: TextView
    private lateinit var CheckOut: TextView
    private lateinit var EmptyCart: TextView
    private lateinit var ScrollView: ScrollView

    private val Details = arrayListOf<CartDetailes>()


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
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ItemsTotal = view.findViewById(R.id.totalFeeTxt)
        DeliveryService = view.findViewById(R.id.deliveryTxt)
        Tax = view.findViewById(R.id.taxTxt)
        TotalPrice = view.findViewById(R.id.totalTxt)
        CheckOut = view.findViewById(R.id.checkOutTxt)
        EmptyCart = view.findViewById(R.id.emptyCartTxt)
        ScrollView = view.findViewById(R.id.ScrollView)
        recyclerView = view.findViewById(R.id.recyclerview)

        Details.add(CartDetailes(R.drawable.pizza,"Pizza",2,100))
        Details.add(CartDetailes(R.drawable.burger,"Burger",12,60))

        if (Details.isEmpty()){
            EmptyCart.visibility = View.VISIBLE
            ScrollView.visibility = View.GONE
        }

        adapter = CartAdapter(Details){
            updateTotalPrice()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        updateTotalPrice()

        CheckOut.setOnClickListener {
            Toast.makeText(context, "Order Placed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateTotalPrice() {
        var itemtotal = 0
        for (item in Details) {
            itemtotal += item.quantity * item.feeEach
        }
        ItemsTotal.text = itemtotal.toString()
        DeliveryService.text = "50"
        Tax.text = "10"

        updateFinalPrice()
    }

    private fun updateFinalPrice() {
        // Convert the text values to integers
        val itemsTotalValue = ItemsTotal.text.toString().toInt()
        val deliveryServiceValue = DeliveryService.text.toString().toInt()
        val taxValue = Tax.text.toString().toInt()

        // Calculate the final total price
        val finalTotalPrice = itemsTotalValue + deliveryServiceValue + taxValue

        TotalPrice.text = "$finalTotalPrice"
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Cart.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Cart().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}