package com.cokchi.sogating_final

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cokchi.sogating_final.auth.UserDataModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CardStackAdapter(val context: Context, val items: List<UserDataModel>) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.profileImageArea)
        val nickname = itemView.findViewById<TextView>(R.id.itemNickname)
        val age = itemView.findViewById<TextView>(R.id.itemAge)
        val city = itemView.findViewById<TextView>(R.id.itemCity)

        // 아이템 데이터를 해당 뷰에 바인딩하는 작업을 수행
        fun binding(data: UserDataModel) {

            //이미지를 glide사용하여 가져오는 로직
            val storageRef = Firebase.storage.reference.child(data.uid + ".png")
            storageRef.downloadUrl.addOnCompleteListener({task ->

                if (task.isSuccessful) {
                    Glide.with(context)
                        .load(task.result)
                        .into(image)
                }
            })

            nickname.text = data.nickname
            age.text = data.age
            city.text = data.city

        }
    }
}
