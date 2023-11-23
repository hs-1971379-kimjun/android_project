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
import com.example.myapplication.databinding.ActivityEditScreenBinding


class EditScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditScreenBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        show()

        binding.updateButton.setOnClickListener {
            updateData()
        }
    }
    private fun show() {
        val key = intent.getStringExtra("itemKey").toString()
        database = FirebaseDatabase.getInstance().getReference("Items").child(key)

        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(ItemModel::class.java)
                binding.title.setText(item?.title)
                binding.description.setText(item?.description)
                binding.price.setText(item?.price)
                binding.soldTF.setText(item?.status)
                binding.name.setText(item?.seller)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "error:불러오지 못했습니다")
            }
        })
    }
    private fun updateData() {
        val key = intent.getStringExtra("itemKey").toString()
        val updatedTitle = binding.title.text.toString()
        val updatedDescription = binding.description.text.toString()
        val updatedPrice = binding.price.text.toString()
        val updateStatus = binding.soldTF.text.toString()

        database = FirebaseDatabase.getInstance().getReference("Items").child(key)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(ItemModel::class.java)


                val updatedData = hashMapOf(
                    "title" to updatedTitle,
                    "description" to updatedDescription,
                    "price" to updatedPrice,
                    "status" to updateStatus,
                    "seller" to item?.seller
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

                Toast.makeText(this@EditScreenActivity, "error: 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }




}