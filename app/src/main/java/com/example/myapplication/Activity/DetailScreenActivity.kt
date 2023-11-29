package com.example.myapplication.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityDetailPageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPageBinding
    private lateinit var database: DatabaseReference
    private lateinit var user : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        show()

        binding.sendMsg.setOnClickListener{

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userEmail", user)
            startActivity(intent)
        }
    }
    fun show() {
        val key = intent.getStringExtra("itemKey").toString()
        val database = FirebaseDatabase.getInstance().getReference("Items").child(key)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(ProductItem::class.java)
                binding.apply {
                    title.text = item?.title
                    description.text = item?.description
                    price.text = item?.price
                    soldTF.text = item?.status
                    name.text = item?.seller
                }
                user = item?.seller?.toString() ?: "DefaultUser"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailScreenActivity, "데이터를 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
