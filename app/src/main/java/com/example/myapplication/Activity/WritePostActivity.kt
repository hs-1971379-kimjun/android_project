package com.example.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WritePostActivity : AppCompatActivity() {

    // Firebase 데이터베이스와 사용자 인터페이스 컴포넌트에 대한 늦은 초기화
    private lateinit var dbRef: DatabaseReference
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        // Firebase 데이터베이스 참조 설정
        dbRef = FirebaseDatabase.getInstance().getReference("Items")

        // Firebase 인증 객체를 통해 현재 로그인된 사용자 정보 가져오기
        val currentUser = FirebaseAuth.getInstance().currentUser

        // UI 컴포넌트 초기화
        initializeUI()

        // 뒤로가기 버튼 이벤트
        backButton.setOnClickListener {
            finish()
        }

        // 업로드 버튼 이벤트
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

    // UI 컴포넌트 초기화를 위한 별도의 함수
    private fun initializeUI() {
        titleEditText = findViewById(R.id.editTextTitle)
        descriptionEditText = findViewById(R.id.editTextDescription)
        priceEditText = findViewById(R.id.editTextPrice)
        backButton = findViewById(R.id.buttonBack)
        uploadButton = findViewById(R.id.buttonUpload)
    }

    // 게시물 업로드 함수
    private fun uploadPost(title: String, description: String, price: String, seller: String?) {
        // 게시물 정보
        val itemData = mapOf(
            "title" to title,
            "description" to description,
            "price" to "$price",
            "status" to "판매 중",
            "seller" to seller
        )

        // 새 게시물 참조 생성 및 데이터베이스에 업로드
        val newItemRef = dbRef.push()
        newItemRef.setValue(itemData)
            .addOnSuccessListener {
                Toast.makeText(this, "업로드 완료", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
            }
    }
}
