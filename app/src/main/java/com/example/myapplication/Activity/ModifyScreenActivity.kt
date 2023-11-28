package com.example.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityModfiyBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ModifyScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModfiyBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModfiyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupUI()

        binding.modify.setOnClickListener {
            navigateToEditScreen()
        }
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = "Modify Item"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupUI() {
        val itemKey = intent.getStringExtra("itemKey").toString()
        database = FirebaseDatabase.getInstance().getReference("Items").child(itemKey)

        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(ProductItem::class.java)
                updateUI(item)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", "Failed to load data")
            }
        })
    }

    private fun updateUI(item: ProductItem?) {
        binding.apply {
            title.text = item?.title
            description.text = item?.description
            price.text = item?.price
            soldTF.text = item?.status
            name.text = item?.seller
        }
    }

    private fun navigateToEditScreen() {
        val itemKey = intent.getStringExtra("itemKey").toString()
        val editIntent = Intent(this, EditScreenActivity::class.java)
        editIntent.putExtra("itemKey", itemKey)
        startActivity(editIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}