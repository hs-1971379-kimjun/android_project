package com.example.myapplication.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Activity.ProductItem
import com.example.myapplication.databinding.ItemBinding
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProductAdapter(private var productList: List<ProductItem>): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        itemClickListener = clickListener
    }

    // 아이템 리스트 갱신하는 함수
    fun updateList(newList: List<ProductItem>) {
        productList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 현재 아이템 가져오기
        val currentItem = productList[position]

        // 뷰홀더에 아이템 타이틀 설정
        holder.binding.itemTitle.text = currentItem.title

        // 리사이클러뷰의 아이템 클릭 리스너 설정
        holder.binding.root.setOnClickListener {
            itemClickListener.onItemClick(position)
        }

        // Check the status and set the appropriate text
        val statusText = if (currentItem.status == "판매 중") {
            "판매 중"
        } else {
            "판매 완료"
        }

        // 뷰홀더에 판매 상태 및 가격 설정
        holder.binding.saleStatus.text = statusText
        holder.binding.itemPrice.text = currentItem.price
    }
}