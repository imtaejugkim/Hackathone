package com.example.hackathoneonebite

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.hackathoneonebite.databinding.ActivityStartBinding
import com.example.hackathoneonebite.main.MainFrameActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class StartActivity : ComponentActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setResultSignUp()
        setGoogleLogin()
        init()
    }

    override fun onStart() {
        super.onStart()
        //로그인 되어 있는 상태면 null이 아닌것을 return
        //로그인이 안 되어 있으면 null을 return
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if(account == null) { //아직 앱에 로그인 하지 않음.
            Toast.makeText(this,"아직 로그인 안됨.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"이미 로그인 됨.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setResultSignUp() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                Toast.makeText(this,result.resultCode.toString(), Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val familyName = account?.familyName.toString()
            val givenName = account?.givenName.toString()
            val displayName = account?.displayName.toString()
            val photoUrl = account?.photoUrl.toString()
            Toast.makeText(this@StartActivity, email, Toast.LENGTH_SHORT).show()
            Log.d("이메일", email)
            Log.d("성", familyName)
            Log.d("이름", givenName)
            Log.d("전체이름", displayName)
            Log.d("프로필사진 주소", photoUrl)
        } catch (e: ApiException) {
            Log.w("failed", "signInResult: failed code = " + e.statusCode)
        }
    }

    fun init() {
        binding.googleSignBtn.setOnClickListener {
            signIn()
        }
        binding.loginBtn.setOnClickListener {
            val i = Intent(this@StartActivity, MainFrameActivity::class.java)
            startActivity(i)
            /*val user = User()
            user.id = binding.userID.text.toString()
            user.pw = binding.userPW.text.toString()
            Log.d("Login Button Clicked", "ID:" + user.id + " / PW:" + user.pw)
            Login(user)*/
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        resultLauncher.launch(signInIntent)
    }

    /*fun Login(user: User){
        val call = RetrofitBuilder.api.getLoginResponse(user)
        call.enqueue(object : Callback<User> { // 비동기 방식 통신 메소드
            override fun onResponse( // 통신에 성공한 경우
                call: Call<User>,
                response: Response<User>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    Log.d("RESPONSE: ", "ID: ${userResponse?.id}, Name: ${userResponse?.pw}")
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@StartActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@StartActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@StartActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }*/
}
