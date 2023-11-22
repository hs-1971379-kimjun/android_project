package com.example.myapplication.Fragment

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var currentFilter: String? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: MutableList<ProductItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val logoutButton = binding.logout
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        val addProductButton = binding.post
        addProductButton.setOnClickListener {
            navigateToAddProduct()
        }

        val filterButton = binding.filter
        filterButton.setOnClickListener {

            PopupMenu(requireContext(), filterButton).apply {
                menuInflater.inflate(R.menu.menu,menu)

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.sell_progress -> selectedStatus = "판매 중"
                        R.id.sell_completed -> selectedStatus = "판매 완료"
                        R.id.menu_show_all -> selectedStatus = null
                        else -> return@setOnMenuItemClickListener false
                    }

                    updateItemList()
                    true
                }

                show()
            }
        }



        val chatButton = binding.chat
        chatButton.setOnClickListener {
            showChatingMenu()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        fetchProductList()
    }

    private fun showLogoutConfirmationDialog() {
        // Implement the logout confirmation dialog
    }

    private fun navigateToAddProduct() {
        // Navigate to the screen to add a new product
    }

    private fun showFilterPopupMenu() {
        // Implement the filter options popup menu

    }

    private fun showChatingMenu() {
        // Implement the filter options popup menu
    }


    private fun applyFilter() {
        val filteredProducts = when (currentFilter) {
            "Active" -> productList.filter { it.status == "Active" }
            "Sold" -> productList.filter { it.status == "Sold" }
            else -> productList
        }
        adapter.updateList(filteredProducts)
    }

    private fun initializeViews() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        productList = mutableListOf()
        adapter = ProductAdapter(productList)
        binding.recyclerView.adapter = adapter
    }

    private fun fetchProductList() {
        // Implement fetching product list from Firebase database
        // Use a ValueEventListener to update the productList and adapter
    }

    // ... (Other functions and callbacks)

    private fun onItemClick(position: Int) {
        // Implement item click logic
    }
}