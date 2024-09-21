package com.gloomdev.restaurantapp.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gloomdev.restaurantapp.databinding.FragmentHistoryBinding
import com.gloomdev.restaurantapp.ui.activities.RecentOrderItems
import com.gloomdev.restaurantapp.ui.adapter.BuyAgainAdapter
import com.gloomdev.restaurantapp.ui.dataclass.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [History.newInstance] factory method to
 * create an instance of this fragment.
 */
class History : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth
    private lateinit var userId:String
    private  var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ):
            View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

// Inflate the layout for this fragment
        retrieveBuyHistory()
        binding.recentbuyItem.setOnClickListener {
            seeItemRecentBuy()
        }

        return binding.root
    }

    private fun seeItemRecentBuy() {
        listOfOrderItem.firstOrNull()?.let { recentBuy->
            val intent = Intent(requireContext(),RecentOrderItems::class.java)
            intent.putExtra("RecentBuyOrderItem",ArrayList(listOfOrderItem))
            startActivity(intent)
        }
    }

    private fun retrieveBuyHistory() {
        binding.recentbuyItem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid?:""

        val buyItemReference:DatabaseReference= database.reference.child("Customers").child("customerDetails").child(userId).child("BuyHistory")
        val shortingQuery:Query = buyItemReference.orderByChild("currentTime")
        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children){
                    val childCount = buySnapshot.childrenCount
//                    if(childCount <= 1){
//                        binding.previousTxt.visibility = View.INVISIBLE
//                    }
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItem.add(it)
                    }
                }
                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()){
                    binding.historyConsLayout.visibility = View.VISIBLE
                    setDataInRecentByItem()
                    setPreviousButItemRecyclerView()
                }else{

                    binding.emptyHistoryTxt.visibility = View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setDataInRecentByItem() {
        binding.recentbuyItem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull()
        recentOrderItem?.let {
            with(binding){
                buyAgainFoodName.text = it.foodNames?.firstOrNull()?:""
                buyAgainFoodPrice.text = it.foodPrices?.firstOrNull()?:""
                val image = it.foodImages?.firstOrNull()?:""
                val uri = Uri.parse(image)
                Glide.with(requireContext())
                    .load(uri)
                    .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
                    .into(buyAgainFoodImage)

                if (listOfOrderItem.isNotEmpty()){

                }


            }
        }
    }

    private fun setPreviousButItemRecyclerView() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()
        for(i in 1 until listOfOrderItem.size){
            listOfOrderItem[i].foodNames?.firstOrNull()?.let {
                buyAgainFoodName.add(it)
            }
            listOfOrderItem[i].foodPrices?.firstOrNull()?.let {
                buyAgainFoodPrice.add(it)
            }
            listOfOrderItem[i].foodImages?.firstOrNull()?.let {
                buyAgainFoodImage.add(it)
            }
        }
        val rv = binding.buyAgainRecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())
        buyAgainAdapter = BuyAgainAdapter(buyAgainFoodName,buyAgainFoodPrice,buyAgainFoodImage,requireContext())
        rv.adapter = buyAgainAdapter

    }


//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_history, container, false)
//    }


}