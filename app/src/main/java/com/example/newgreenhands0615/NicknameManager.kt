package com.example.newgreenhands0615

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object NicknameManager{
    fun checkNicknameExists(nickname: String, callback: (Boolean) -> Unit) {
        FBRef.userRef.orderByChild("nickname").equalTo(nickname).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callback(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false)
            }
        })
    }

    // 이전에 생성된 닉네임이 있는지 확인하고, 없다면 callback을 호출한다.
    fun createNicknameWithCheck(nickname: String, callback: (Boolean) -> Unit) {
        checkNicknameExists(nickname) { nicknameExists ->
            if (nicknameExists) {
                callback(false)
            } else {
                // 닉네임이 존재하지 않으면 생성
                callback(true)
            }
        }
    }


}