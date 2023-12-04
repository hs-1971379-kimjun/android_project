package com.example.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityDetailPageBinding
import com.google.firebase.database.*

class DetailPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPageBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sellerEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        showItemDetails()
        setSendMsgClickListener()
    }

    private fun setupUI() {
        sellerEmail = intent.getStringExtra("itemKey").toString()
        databaseReference = FirebaseDatabase.getInstance().getReference("Items").child(sellerEmail)
    }

    private fun showItemDetails() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(ProductItem::class.java)
                item?.let {
                    with(binding) {
                        title.text = it.title
                        description.text = it.description
                        price.text = it.price
                        soldTF.text = it.status
                        name.text = it.seller
                    }
                    sellerEmail = it.seller ?: "DefaultUser"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showMessage("데이터를 불러오지 못했습니다")
            }
        })
    }

    private fun setSendMsgClickListener() {
        binding.sendMsg.setOnClickListener {
            val intent = Intent(this, ChatScreenActivity::class.java)
            intent.putExtra("userEmail", sellerEmail)
            startActivity(intent)
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@DetailPageActivity, message, Toast.LENGTH_SHORT).show()
    }
}
