package com.example.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showProductDetails()

        binding.updateButton.setOnClickListener {
            updateProductData()
        }
    }

    private fun showProductDetails() {
        val productKey = intent.getStringExtra("itemKey").toString()
        database = FirebaseDatabase.getInstance().getReference("Items").child(productKey)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productItem = snapshot.getValue(ProductItem::class.java)
                binding.title.setText(productItem?.title)
                binding.description.setText(productItem?.description)
                binding.price.setText(productItem?.price)
                binding.soldTF.setText(productItem?.status)
                binding.name.setText(productItem?.seller)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "error: 불러오지 못했습니다")
            }
        })
    }

    private fun updateProductData() {
        val productKey = intent.getStringExtra("itemKey").toString()
        val titleUpdate = binding.title.text.toString()
        val descriptionUpdate = binding.description.text.toString()
        val priceUpdate = binding.price.text.toString()
        val statsUpdate = binding.soldTF.text.toString()

        database = FirebaseDatabase.getInstance().getReference("Items").child(productKey)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentProduct = snapshot.getValue(ProductItem::class.java)

                val updatedData = hashMapOf(
                    "title" to titleUpdate,
                    "description" to descriptionUpdate,
                    "price" to priceUpdate,
                    "status" to statsUpdate,
                    "seller" to currentProduct?.seller
                )

                database.setValue(updatedData)
                    .addOnSuccessListener {
                        val resultIntent = Intent()
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}