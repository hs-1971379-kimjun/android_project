package com.example.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WritePostActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var backButton2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.writepost)


        dbRef = FirebaseDatabase.getInstance().getReference("Items")

        val mAuth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = mAuth.currentUser

        titleEditText = findViewById(R.id.cTitle)
        descriptionEditText = findViewById(R.id.cDes)
        priceEditText = findViewById(R.id.cPrice)
        backButton2 = findViewById(R.id.backbutton2)
        backButton2.setOnClickListener{
            finish()
        }
        uploadButton = findViewById(R.id.writepostbtn)
        uploadButton.setOnClickListener(View.OnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val price = priceEditText.text.toString()
            val userName = currentUser?.email

            // 글 작성 및 업로드
            uploadPost(title, description, price, userName)
            finish()
        })
    }


    private fun uploadPost(title: String?, description: String?, price: String?, seller: String?) {
        val formattedPrice = "$price"
        val itemStatus = "판매 중"

        val itemData = hashMapOf(
            "title" to title,
            "description" to description,
            "price" to formattedPrice,
            "status" to itemStatus,
            "seller" to seller
        )

        val newItemRef = dbRef.push()
        newItemRef.setValue(itemData)
            .addOnSuccessListener {
                val documentId = newItemRef.key
                Toast.makeText(this,"업로드 완료",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,"업로드 실패",Toast.LENGTH_SHORT).show()
            }
    }
}
