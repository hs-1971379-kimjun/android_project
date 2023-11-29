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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrationFormActivity : AppCompatActivity() {
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var mEtEmail: EditText
    private lateinit var mEtPwd: EditText
    private lateinit var mEtName: EditText
    private lateinit var mEtBirth: EditText
    private lateinit var mBtnRegister: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_form)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().reference

        mEtEmail = findViewById(R.id.createEmail)
        mEtPwd = findViewById(R.id.createPWD)
        mEtName = findViewById(R.id.create_name)
        mEtBirth = findViewById(R.id.create_birth)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        mBtnRegister = findViewById(R.id.CreateButton)
        mBtnRegister.setOnClickListener {
            val email = mEtEmail.text.toString()
            val password = mEtPwd.text.toString()
            val name = mEtName.text.toString()
            val birth = mEtBirth.text.toString()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || birth.isEmpty()) {
                Toast.makeText(this, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = mFirebaseAuth.currentUser
                        val account = UserData().apply {
                            idToken = firebaseUser?.uid
                            emailId = firebaseUser?.email
                            this.password = password
                            this.name = name
                            this.birth = birth
                        }

                        mDatabaseRef.child("UserAccount").child(firebaseUser?.uid ?: "").setValue(account)

                        Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        Toast.makeText(this, "회원가입 실패!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}