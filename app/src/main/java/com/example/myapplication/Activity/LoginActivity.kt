package com.example.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.myapplication.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // 회원가입 버튼 이벤트
        binding.createBtn.setOnClickListener {
            navigateToCreateActivity()
        }

        // 로그인 버튼 이벤트
        binding.loginBtn.setOnClickListener {
            performLogin()
        }
    }

    // 회원가입 액티비티로 네비게이션
    private fun navigateToCreateActivity() {
        val intent = Intent(this, CreateActivity::class.java)
        startActivity(intent)
    }

    // 로그인 수행
    private fun performLogin() {
        val email = binding.email.text.toString()
        val pwd = binding.pwd.text.toString()

        auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) { // 로그인 성공
                navigateToMainActivity()
            } else { // 로그인 실패
                Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 메인 액티비티로 네비게이션
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}