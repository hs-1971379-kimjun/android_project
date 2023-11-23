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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

    private fun fetchProductList() {
        val itemRecyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        itemRecyclerView?.visibility = View.GONE

        var keys = mutableListOf<String>()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val (itemList, key) = getItemListFromFirebase()
                keys = key.toMutableList()
                adapter.updateList(itemList)
                binding.recyclerView.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.d("오류", "error: 불러오기 실패")
            }
        }
        val userEmail = FirebaseAuth.getInstance().currentUser?.email //현재 사용자의 email

        //리사이클러뷰의 아이템 클릭 이벤트
        adapter.setOnItemClickListener(object : ProductAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val ClickedItemSeller = productList[position].seller.toString()

                if(userEmail == ClickedItemSeller) { //현재 사용자의 email과 글 작성자 email 동일하면 수정 화면으로 이동
                    val intent = Intent(requireContext(), ModifyScreenActivity::class.java)
                    val itemKey = keys[position] // 클릭한 아이템의 고유 키값 가져오기
                    intent.putExtra("itemKey", itemKey) // 클릭한 아이템의 고유 키값을 Intent에 넣어서 전달
                    startActivity(intent)

                } else { //다르면 판매 글 보기 화면으로 이동
                    val intent2 = Intent(requireContext(), DetailScreenActivity::class.java)
                    val itemKey = keys[position] // 클릭한 아이템의 고유 키값 가져오기
                    intent2.putExtra("itemKey", itemKey) // 클릭한 아이템의 고유 키값을 Intent에 넣어서 전달
                    startActivity(intent2)
                }
            }
        })
    }

    private suspend fun getItemListFromFirebase(): Pair<List<ProductItem>, List<String>> {
        val storageRef = FirebaseDatabase.getInstance().reference.child("Items")
        val userEmail = FirebaseAuth.getInstance().currentUser?.email // 현재 사용자의 email

        val snapshot = if (status != null) {
            // 선택된 상태에 따라 Firebase 쿼리 설정
            storageRef.orderByChild("status").equalTo(status).get().await()
        } else {
            storageRef.get().await() // 모든 항목 가져오기
        }

        val itemList = mutableListOf<ProductItem>()
        val keys = mutableListOf<String>()

        if (snapshot.exists()) {
            for (itemSnap in snapshot.children) {
                val key = itemSnap.key
                keys.add(key!!) // 고유 키값을 keys 리스트에 추가

                val itemData = itemSnap.getValue(ProductItem::class.java)
                itemList.add(itemData!!)
            }
        }

        return  Pair(itemList, keys)
    }

}