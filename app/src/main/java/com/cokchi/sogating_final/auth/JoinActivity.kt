package com.cokchi.sogating_final.auth

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.cokchi.sogating_final.MainActivity
import com.cokchi.sogating_final.R
import com.cokchi.sogating_final.utils.FirebaseRef
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class JoinActivity : AppCompatActivity() {

    private val TAG = "JoinActivity"

    private lateinit var auth: FirebaseAuth

    private var nickname = ""
    private var gender = ""
    private var city = ""
    private var age = ""
    private var uid = ""

    lateinit var profileImage : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        auth = Firebase.auth

        profileImage = findViewById(R.id.imageArea)

        //이미지등록,불러오기 로직
        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { result ->
                if (result != null) {
                    profileImage.setImageURI(result)
                }
            }
        )

        profileImage.setOnClickListener {
            getAction.launch("image/*")
        }

        val joinBtn = findViewById<Button>(R.id.joinBtn)
        joinBtn.setOnClickListener {

            val emailArea = findViewById<TextInputEditText>(R.id.emailArea)
            val pwdArea = findViewById<TextInputEditText>(R.id.pwdArea)

            //TODO로직 ex)비밀번호 중복체크, 각 값들의 널값 체크
//            val emailCheck = emailArea.text.toString()
//            if (emailCheck.isEmpty()) {
//                Toast.makeText(this,"비어있음", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(this,"비어있지않음", Toast.LENGTH_LONG).show()
//            }

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

                        //회원가입이되면서 이미지를 업로드
                        uploadImage(uid)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        // Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        // updateUI(null)
                    }
                }
        }
    }

    //이미지업로드로직
    private fun uploadImage(uid : String) {

        val storage = Firebase.storage
        //저장장소지정
        val storageRef = storage.reference.child(uid + ".png")

        // Get the data from an ImageView as bytes
        profileImage.isDrawingCacheEnabled = true
        profileImage.buildDrawingCache()
        val bitmap = (profileImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }
}
