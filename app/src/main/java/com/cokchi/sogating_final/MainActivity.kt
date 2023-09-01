package com.cokchi.sogating_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.cokchi.sogating_final.auth.UserDataModel
import com.cokchi.sogating_final.setting.SettingActivity
import com.cokchi.sogating_final.utils.FirebaseAuthUtils
import com.cokchi.sogating_final.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager: CardStackLayoutManager

    private val TAG = "MainActivity"

    private val usersDataList = mutableListOf<UserDataModel>()

    private var userCount = 0

    private lateinit var currentUserGender: String

    private val uid = FirebaseAuthUtils.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    //내가(B) A를 좋아하는데 A의 좋아요 리스트에 내가(B) 있는지 확인하면됨

        val setting = findViewById<ImageView>(R.id.settingIcon)
        setting.setOnClickListener {

            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        val cardStackView = findViewById<CardStackView>(R.id.cardStackView)

        manager = CardStackLayoutManager(baseContext, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {

                if (direction == Direction.Right) {
//                    Toast.makeText(this@MainActivity, "right", Toast.LENGTH_SHORT).show()
                    //현재유저의 UID값을 로그찍어보기
//                    Log.d(TAG, usersDataList[userCount].uid.toString())

                    userLikeOtherUser(uid, usersDataList[userCount].uid.toString())
                }
                if (direction == Direction.Left) {
//                    Toast.makeText(this@MainActivity, "left", Toast.LENGTH_SHORT).show()
                }

                userCount = userCount + 1

                if (userCount == usersDataList.count()) {
                    getUserDataList(currentUserGender)
                    Toast.makeText(this@MainActivity, "유저를 새롭게 받아오는중", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }
        })

        cardStackAdapter = CardStackAdapter(baseContext, usersDataList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = cardStackAdapter

        // 유저 데이터를 리스트에 담음
        // getUserDataList()
        // 나의 성별 가져와서 담기
        getMyUserData()
    }

    private fun getMyUserData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, dataSnapshot.toString())
                val data = dataSnapshot.getValue(UserDataModel::class.java)

                Log.d(TAG, data?.gender.toString())

                currentUserGender = data?.gender.toString()

                getUserDataList(currentUserGender)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        // 파이어베이스 안에 UID 정보를 가져옴
        FirebaseRef.userInfoRef.child(uid).addListenerForSingleValueEvent(postListener)
    }

    // 회원 정보 받아오기
    private fun getUserDataList(currentUserGender: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    val user = dataModel.getValue(UserDataModel::class.java)

                    // 성별이 현재 유저의 성별과 같을 때와 (if) 다를 때 (else)
                    if (user!!.gender.toString().equals(currentUserGender)) {
                    } else {
                        usersDataList.add(user!!)
                    }
                }

                // 데이터를 받아온 뒤 다시 한 번 동기화
                cardStackAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }

    //유저의 좋아요를 표시하는 로직
    //나의 UID 값, 좋아요한 사람의 UID값
    private fun userLikeOtherUser(myUid : String, otherUid : String) {
        FirebaseRef.userLikeRef.child(myUid).child(otherUid).setValue("true")
        getOtherUserLikeList(otherUid)
    }

    //내가 좋아요한사람이 누구를 좋아요 했는지 알수있음
    private fun getOtherUserLikeList(otherUid: String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //여기 리스트안에서 나의 UID가 있는지 확인
                for (dataModel in dataSnapshot.children) {
                    val likeUserKey = dataModel.key.toString()
                    if (likeUserKey.equals(uid)) {
                        Toast.makeText(this@MainActivity, "매칭 완료", Toast.LENGTH_SHORT)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)
    }

}

