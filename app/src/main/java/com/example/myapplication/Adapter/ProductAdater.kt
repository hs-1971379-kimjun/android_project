package com.example.myapplication.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Activity.ProductItem
import com.example.myapplication.databinding.ItemBinding
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

// ViewHolder 클래스를 ItemAdapter 밖으로 분리
class ItemViewHolder(var binding: ItemBinding) : RecyclerView.ViewHolder(binding.root)

// RecyclerView의 아이템들을 관리하기 위한 어댑터 클래스
class ProductAdater(
    private var itemList: List<ProductItem>,
    private val itemClickListener: (Int) -> Unit // 클릭 리스너를 람다 표현식으로 받음
) : RecyclerView.Adapter<ItemViewHolder>() {

    // 새로운 데이터  아이템 리스트를 업데이트
    fun updateList(newList: List<ProductItem>) {
        itemList = newList
        notifyDataSetChanged()
    }

    // ViewHolder 생성을 위한 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    // 전체 아이템 개수 반환
    override fun getItemCount(): Int = itemList.size

    // 각 아이템을 바인딩하는 함수
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        with(holder.binding) {
            loadImage(itemImg, "image/item1.jpg")
            itemTitle.text = currentItem.title
            itemStatus.text = if (currentItem.status == "판매 중") "판매 중" else "판매 완료"
            itemPrice.text = currentItem.price

            // 클릭 리스너를 람다 표현식으로 설정
            root.setOnClickListener { itemClickListener(position) }
        }
    }

    // 이미지 로딩을 별도의 함수로 분리
    private fun loadImage(imageView: ImageView, imagePath: String) {
        val imageReference = FirebaseStorage.getInstance().reference.child(imagePath)
        imageReference.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(imageView)
        }.addOnFailureListener {
            // 이미지 로드 실패 시 처리
        }
    }
}