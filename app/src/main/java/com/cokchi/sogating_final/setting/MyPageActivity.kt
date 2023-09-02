package com.cokchi.sogating_final.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cokchi.sogating_final.R
import com.cokchi.sogating_final.auth.UserDataModel
import com.cokchi.sogating_final.utils.FirebaseAuthUtils
import com.cokchi.sogating_final.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyPageActivity : AppCompatActivity() {

    private val TAG = "MyPageActivity"
    private val uid = FirebaseAuthUtils.getUid()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        getMydata()
    }
    private fun getMydata() {

        //데이터스냅샷에 있는 데이터를 각각가져오기
        val myImage = findViewById<ImageView>(R.id.myImage)
        val myUid = findViewById<TextView>(R.id.myUid)
        val myNickname = findViewById<TextView>(R.id.myNickname)
        val myAge = findViewById<TextView>(R.id.myAge)
        val myCity = findViewById<TextView>(R.id.myCity)
        val myGender = findViewById<TextView>(R.id.myGender)


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, dataSnapshot.toString())

                // 데이터스냅샷에 있는 데이터를 가져오고, null 체크를 하여 안전하게 접근
                val data = dataSnapshot.getValue(UserDataModel::class.java)

                if (data != null) {
                    myUid.text = data.uid
                    myNickname.text = data.nickname
                    myAge.text = data.age
                    myCity.text = data.city
                    myGender.text = data.gender

                    // 이미지 가져오기
                    val storageRef = Firebase.storage.reference.child("${data.uid}.png")
                    storageRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Glide.with(baseContext)
                                .load(task.result)
                                .into(myImage)
                        } else {
                            // 이미지 다운로드 실패 시 처리
                        }
                    }
                } else {
                    // data가 null인 경우에 대한 처리를 추가할 수 있습니다.
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)

    }
}