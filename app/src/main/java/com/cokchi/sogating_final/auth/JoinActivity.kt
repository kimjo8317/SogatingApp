package com.cokchi.sogating_final.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.cokchi.sogating_final.R
import com.cokchi.sogating_final.utils.FirebaseRef
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {

    private val TAG = "JoinActivity"

    private lateinit var auth: FirebaseAuth

    private var nickname = ""
    private var gender = ""
    private var city = ""
    private var age = ""
    private var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val joinBtn = findViewById<Button>(R.id.joinBtn) // 수정: joinBtn ID로 변경
        joinBtn.setOnClickListener {

            auth = Firebase.auth

            val emailArea = findViewById<TextInputEditText>(R.id.emailArea) // 수정: emailArea ID로 변경
            val pwdArea = findViewById<TextInputEditText>(R.id.pwdArea) // 수정: pwdArea ID로 변경

            //필요한정보를 activity에서 가져와서 닉네임 성별 지역 나이 (UID값) 추가
            gender = findViewById<TextInputEditText>(R.id.genderArea).text.toString()
            city = findViewById<TextInputEditText>(R.id.cityArea).text.toString()
            age = findViewById<TextInputEditText>(R.id.ageArea).text.toString()
            nickname = findViewById<TextInputEditText>(R.id.nicknameArea).text.toString()

            auth.createUserWithEmailAndPassword(emailArea.text.toString(), pwdArea.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                         val user = auth.currentUser
                        //(UID값)+모델값
                        uid = user?.uid.toString()
                        val userModel = UserDataModel(
                            uid,
                            nickname,
                            age,
                            gender,
                            city
                        )

                        //Utills FirebaseRef에서 가져오고(형식:uid,값) Model에서 각각의값을 가져와서 DB에추가
                        FirebaseRef.userInfoRef.child(uid).setValue(userModel)


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
