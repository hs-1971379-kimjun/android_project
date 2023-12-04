@file:Suppress("DEPRECATION")
package com.example.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Data.UserData
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationFormActivity : AppCompatActivity() {

    // Firebase 인증 및 데이터베이스 참조를 지연 초기화(lazy)로 설정
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val databaseReference by lazy { FirebaseDatabase.getInstance().reference }

    // UI 요소들을 지연 초기화로 설정
    private val emailEditText by lazy { findViewById<EditText>(R.id.createEmail) }
    private val passwordEditText by lazy { findViewById<EditText>(R.id.createPWD) }
    private val nameEditText by lazy { findViewById<EditText>(R.id.create_name) }
    private val birthEditText by lazy { findViewById<EditText>(R.id.create_birth) }
    private val registerButton by lazy { findViewById<Button>(R.id.CreateButton) }
    private val backButton by lazy { findViewById<Button>(R.id.backButton) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_form)

        backButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    // 사용자 등록 로직을 별도의 메소드로 분리
    private fun registerUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val name = nameEditText.text.toString()
        val birth = birthEditText.text.toString()

        if (email.isBlank() || password.isBlank() || name.isBlank() || birth.isBlank()) {
            Toast.makeText(this, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    saveUserData(user?.uid, email, password, name, birth)
                    navigateToMain()
                } else {
                    Toast.makeText(this, "회원가입 실패!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Firebase 데이터베이스에 사용자 정보 저장을 별도의 메소드로 분리
    private fun saveUserData(uid: String?, email: String, password: String, name: String, birth: String) {
        val userAccount = UserData().apply {
            idToken = uid
            emailId = email
            this.password = password
            this.name = name
            this.birth = birth
        }
        databaseReference.child("UserAccount").child(uid ?: return).setValue(userAccount)
    }

    // 메인 액티비티로 이동하는 로직을 별도의 메소드로 분리
    private fun navigateToMain() {
        Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}