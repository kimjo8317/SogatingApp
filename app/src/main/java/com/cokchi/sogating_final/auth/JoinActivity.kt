package com.cokchi.sogating_final.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.cokchi.sogating_final.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {

    private val TAG = "JoinActivity"

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val joinBtn = findViewById<Button>(R.id.joinBtn) // 수정: joinBtn ID로 변경
        joinBtn.setOnClickListener {

            auth = Firebase.auth

            val emailArea = findViewById<TextInputEditText>(R.id.emailArea) // 수정: emailArea ID로 변경
            val pwdArea = findViewById<TextInputEditText>(R.id.pwdArea) // 수정: pwdArea ID로 변경

            auth.createUserWithEmailAndPassword(emailArea.text.toString(), pwdArea.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                         val user = auth.currentUser
                        Log.d(TAG, user?.uid.toString())
                        // updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        // Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        // updateUI(null)
                    }
                }
        }
    }
}
