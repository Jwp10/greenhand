package com.example.newgreenhands0615

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.newgreenhands0615.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val binding: ActivityJoinBinding by lazy {
        ActivityJoinBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        auth = Firebase.auth

        signUp()


    }

    //회원가입하기
    //참고 https://firebase.google.com/docs/auth/android/start?hl=ko#kotlin+ktx_1
    // createuserwithemailandpassword 사용하면 됨.
    private fun signUp() {

        //최초 linearlayout을 안보이게 만듦으로써, 인증안하면 회원가입 불가능하게 설정
        binding.checkLinear.visibility = View.INVISIBLE
        binding.verificationLinear.visibility = View.INVISIBLE


        //중복 버튼 눌렀을시
        binding.checkBtn.setOnClickListener {
            val email = binding.phonenumberArea.text.toString()
            if (!email.endsWith("@knu.ac.kr")) {
                Toast.makeText(this, "경북대학교 웹 메일만 사용 가능합니다", Toast.LENGTH_SHORT).show()

            } else {
                val builder = AlertDialog.Builder(this)
                binding.checkBtn.setOnClickListener {
                    val email = binding.phonenumberArea.text.toString()
                    auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                        // 새로운 사람인지 확인
                        val isNewUser = task.result.signInMethods!!.isEmpty()
                        if (isNewUser) {
                            Toast.makeText(this, email + "로 인증 메일을 보냈습니다", Toast.LENGTH_SHORT)
                                .show()
                            binding.verificationLinear.visibility = View.VISIBLE
                           // check_VerificationCode()
                        } else {
                            builder.setMessage("사용 할 수 없는 이메일입니다.")
                                .setPositiveButton(
                                    "확인",
                                    DialogInterface.OnClickListener { dialog, id -> })
                            builder.create()
                            builder.show()
                        }
                    }
                }
            }
        }
        binding.nicknameBtn.setOnClickListener {
            val nickname = binding.nicknameArea.text.toString()
            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력하세요", Toast.LENGTH_SHORT).show()
            } else {
                NicknameManager.checkNicknameExists(nickname) { nicknameExists ->
                    if (nicknameExists) {
                        binding.nicknameArea.setText("")
                        Toast.makeText(this, "이미 사용 중인 닉네임입니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "사용 가능한 닉네임입니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.joinBtn.setOnClickListener() {
            var isGoToJoin = true
            val email = binding.phonenumberArea.text.toString()
            val nick = binding.nicknameArea.text.toString()
            val password1 = binding.passwordArea1.text.toString()
            val password2 = binding.passwordArea2.text.toString()


            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }
            if (password1.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }
            if(nick.isEmpty())
            {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }
            if (password2.isEmpty()) {
                Toast.makeText(this, "비밀번호 확인을 입력해주세요", Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }
            if (password1 != password2) {
                Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }

//            if (!email.endsWith("@knu.ac.kr")) {
//                Toast.makeText(this, "경북대학교 웹 메일만 사용 가능합니다", Toast.LENGTH_SHORT).show()
//                isGoToJoin = false
//            }

            if (password1.length < 6) {
                Toast.makeText(this, "비밀번호는 여섯자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }

            NicknameManager.createNicknameWithCheck(binding.nicknameArea.text.toString()) { nicknameCreated ->
                if (nicknameCreated) {
                    // 닉네임 생성 성공
                    isGoToJoin=true
                    //  FBRef.userRef.child(FBAuth.getUid()).setValue(UserModel(FBAuth.getUid(), binding.nicknameArea.text.toString()))
                    Toast.makeText(this, "닉네임 생성 성공", Toast.LENGTH_SHORT).show()
                } else {
                    isGoToJoin=false
                    // 닉네임 생성 실패
                    Toast.makeText(this, "이미 사용 중인 닉네임입니다", Toast.LENGTH_SHORT).show()
                }
            }
            if (isGoToJoin) {
                auth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show()
                            FBRef.nickRef.child(FBAuth.getUid()).setValue(UserModel(FBAuth.getUid(), binding.nicknameArea.text.toString()))
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            }


        }
    }

//    private fun check_VerificationCode() {
//
//        val verification = GMailSender().sendEmail(binding.emailArea.text.toString())
//        binding.verificationBtn.setOnClickListener {
//            if (binding.verificationArea.text.toString() == verification) {
//                Toast.makeText(this, "인증번호가 일치합니다", Toast.LENGTH_SHORT).show()
//                binding.checkLinear.visibility = View.VISIBLE
//            } else {
//                Toast.makeText(this, "인증번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
////                Toast.makeText(this,binding.verificationArea.text.toString(),Toast.LENGTH_SHORT).show()
////                Toast.makeText(this,GMailSender().code,Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    }

}