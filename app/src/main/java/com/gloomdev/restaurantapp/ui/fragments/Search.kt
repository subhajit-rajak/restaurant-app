package com.gloomdev.restaurantapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gloomdev.restaurantapp.databinding.FragmentSearchBinding
import com.gloomdev.restaurantapp.ui.adapter.MenuItemsAdapter
import com.gloomdev.restaurantapp.ui.dataclass.MenuItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Search : Fragment() {

    private var filteredList = arrayListOf<MenuItems>()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var menuAdapter: MenuItemsAdapter
    private lateinit var menuList: MutableList<MenuItems>
    private lateinit var userId: String
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSearchBinding.inflate(layoutInflater)
        menuList = mutableListOf()
        menuAdapter = MenuItemsAdapter(menuList,requireContext())
        binding.searchRecyclerView.adapter = menuAdapter
        auth = FirebaseAuth.getInstance()
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(context)
        database = FirebaseDatabase.getInstance()
        fetchDataFromFirebase()

        // Add TextWatcher to the search EditText for filtering the list as the user types
        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    // Function to filter the restaurant list based on the search query
    private fun filterList(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(menuList)
        } else {
            for (menu in menuList) {
                if (menu.foodName?.contains(query, ignoreCase = true) == true) {
                    filteredList.add(menu)
                }
            }
        }
        menuAdapter.updateList(filteredList)
    }
private fun fetchDataFromFirebase() {
    userId = auth.currentUser?.uid ?: ""
    //menuList = mutableListOf()
    val menuRef = database.getReference("menu").child(userId)
    menuRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            menuList.clear()
            for (menuSnapshot in snapshot.children) {
                val menuItem = menuSnapshot.getValue(MenuItems::class.java)
                menuItem?.let { menuList.add(it) }
            }
            setAdapter()
           menuAdapter.notifyDataSetChanged()
        }
        private fun setAdapter() {
            if (menuList.isNotEmpty()) {
                val adapter = MenuItemsAdapter(menuList, requireContext())
                binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.searchRecyclerView.adapter = adapter
            }
        }
        override fun onCancelled(error: DatabaseError) {
            //Toast.makeText(this@Search, "Failed to load data", Toast.LENGTH_SHORT).show()
        }
    })
}
}
