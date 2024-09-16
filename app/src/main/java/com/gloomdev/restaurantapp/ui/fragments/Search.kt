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
        filteredList = arrayListOf()
        menuAdapter = MenuItemsAdapter(filteredList, requireContext())
        binding.searchRecyclerView.adapter = menuAdapter
        auth = FirebaseAuth.getInstance()
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(context)
        database = FirebaseDatabase.getInstance()
        fetchDataFromFirebase()

        // Add TextWatcher to searchView
        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private fun fetchDataFromFirebase() {
        userId = auth.currentUser?.uid ?: ""

        val menuRef = database.getReference("menu").child(userId)
        menuRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuList.clear()
                for (menuSnapshot in snapshot.children) {
                    val menuItem = menuSnapshot.getValue(MenuItems::class.java)
                    menuItem?.let { menuList.add(it) }
                }
                filteredList.clear()
                filteredList.addAll(menuList)
                menuAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    // Filter the list based on the query
    private fun filterList(query: String) {
        val filtered = menuList.filter {
            it.foodName.contains(query,ignoreCase = true)
        }
        filteredList.clear()
        filteredList.addAll(filtered)
        menuAdapter.notifyDataSetChanged()
    }
}
