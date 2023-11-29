package com.example.myapplication.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreatePostActivity : AppCompatActivity() {


    private lateinit var dbRef: DatabaseReference
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)


        dbRef = FirebaseDatabase.getInstance().getReference("Items")


        val currentUser = FirebaseAuth.getInstance().currentUser


        initializeUI()


        backButton.setOnClickListener {
            finish()
        }


        uploadButton.setOnClickListener {
            uploadPost(
                title = titleEditText.text.toString(),
                description = descriptionEditText.text.toString(),
                price = priceEditText.text.toString(),
                seller = currentUser?.email

            )
            finish()
        }
    }


    private fun initializeUI() {
        titleEditText = findViewById(R.id.editTextTitle)
        descriptionEditText = findViewById(R.id.editTextDescription)
        priceEditText = findViewById(R.id.editTextPrice)
        backButton = findViewById(R.id.buttonBack)
        uploadButton = findViewById(R.id.buttonUpload)
    }


    private fun uploadPost(title: String, description: String, price: String, seller: String?) {

        val itemData = mapOf(
            "title" to title,
            "description" to description,
            "price" to "$price",
            "status" to "판매 중",
            "seller" to seller
        )
        println("게시글 작성 완료 ${itemData}")

       
        val newItemRef = dbRef.push()
        newItemRef.setValue(itemData)
            .addOnSuccessListener {
                println("게시글 작성 완료3")
                Toast.makeText(this, "업로드 완료", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                println("게시글 작성 완료4")
                Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
            }
        println("게시글 작성 완료2")
    }
}
