package com.example.myapplication.Fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Activity.DetailScreenActivity
import com.example.myapplication.Activity.ProductItem
import com.example.myapplication.Activity.LoginActivity
import com.example.myapplication.Activity.ModifyScreenActivity
import com.example.myapplication.Activity.ShowChatActivity
import com.example.myapplication.Activity.WritePostActivity
import com.example.myapplication.Adapter.ProductAdapter
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: MutableList<ProductItem>
    private var currentFilter: String? = null
    private var status: String? = null

    private lateinit var keys: List<String>


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
                        R.id.sell -> status = "판매 중"
                        R.id.sold -> status = "판매 완료"
                        R.id.allProduct -> status = null
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

    private fun updateItemList() {
        val filteredItems = when (status) {
            "판매 중" -> {
                productList.filter { it.status == "판매 중" }
            }
            "판매 완료" -> {
                productList.filter { it.status == "판매 완료" }
            }
            else -> {
                productList
            }
        }
        Log.d(status, "")
        adapter.updateList(filteredItems)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        fetchProductList()
    }

    private fun showLogoutConfirmationDialog() {
        val myDialog = Dialog(requireContext())
        myDialog.setContentView(layoutInflater.inflate(R.layout.logout_dialog, null))

        myDialog.setCancelable(true)
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val logoutMsg: TextView = myDialog.findViewById(R.id.logoutMsg)
        val btnYes: Button = myDialog.findViewById(R.id.btnYes)
        val btnNo: Button = myDialog.findViewById(R.id.btnNo)

        logoutMsg.text = "로그아웃 하시겠습니까?"

        btnYes.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)

            Toast.makeText(requireContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
            myDialog.dismiss()
        }

        btnNo.setOnClickListener {
            myDialog.dismiss()
        }

        myDialog.show()
    }

    private fun navigateToAddProduct() {
        // Navigate to the screen to add a new product
        val intent = Intent(this@HomeFragment.requireActivity(), WritePostActivity::class.java)
        startActivity(intent)
    }

    private fun showChatingMenu() {
        // Implement the filter options popup menu
        val intent = Intent(requireContext(), ShowChatActivity::class.java)
        startActivity(intent)
    }


    private fun applyFilter() {
        val filteredProducts = when (currentFilter) {
            "Active" -> productList.filter { it.status == "Active" }
            "Sold" -> productList.filter { it.status == "Sold" }
            else -> productList
        }
        adapter.updateList(filteredProducts)
    }


    override fun onResume() {
        super.onResume()
        fetchProductList()
    }

    private fun initializeViews() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        productList = mutableListOf()
        adapter = ProductAdapter(productList)
        binding.recyclerView.adapter = adapter
    }

    private fun setupRecyclerView() {
        val itemRecyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        itemRecyclerView?.visibility = View.GONE
    }

    private fun setupDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("Items")
    }

    private fun setupDatabaseQuery(): Query {
        return if (status != null) {
            databaseReference.orderByChild("status").equalTo(status)
        } else {
            databaseReference
        }
    }

    private fun fetchKeysFromSnapshot(snapshot: DataSnapshot): List<String> {
        val keys = mutableListOf<String>()
        for (itemSnap in snapshot.children) {
            val key = itemSnap.key
            keys.add(key!!)
        }
        return keys
    }

    private fun fetchProductDataFromSnapshot(snapshot: DataSnapshot): MutableList<ProductItem> {
        val productList = mutableListOf<ProductItem>()
        for (itemSnap in snapshot.children) {
            val itemData = itemSnap.getValue(ProductItem::class.java)
            productList.add(itemData!!)
        }
        return productList
    }

    private fun updateUI(productList: MutableList<ProductItem>) {
        adapter.updateList(productList)
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun handleDatabaseError(error: DatabaseError) {
        Log.d("오류", "error:불러오기 실패")
    }

    private fun getCurrentUserEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }

    private fun navigateToScreen(itemKey: String, isCurrentUser: Boolean) {
        val intent = if (isCurrentUser) {
            Intent(requireContext(), ModifyScreenActivity::class.java)
        } else {
            Intent(requireContext(), DetailScreenActivity::class.java)
        }
        intent.putExtra("itemKey", itemKey)
        startActivity(intent)
    }

    private fun setItemClickListener() {
        adapter.setOnItemClickListener(object : ProductAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val clickedItemSeller = productList[position].seller.toString()
                val isCurrentUser = getCurrentUserEmail() == clickedItemSeller
                val itemKey = keys[position]
                navigateToScreen(itemKey, isCurrentUser)
            }
        })
    }

    private fun fetchDataFromDatabase() {
        val query = setupDatabaseQuery()

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                keys = fetchKeysFromSnapshot(snapshot)
                productList = fetchProductDataFromSnapshot(snapshot)
                updateUI(productList)
            }

            override fun onCancelled(error: DatabaseError) {
                handleDatabaseError(error)
            }
        })
    }



    private fun fetchProductList() {
        setupRecyclerView()
        databaseReference = setupDatabaseReference()
        fetchDataFromDatabase()
        setItemClickListener()
    }
}