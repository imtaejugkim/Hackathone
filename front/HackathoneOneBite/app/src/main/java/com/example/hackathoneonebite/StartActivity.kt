package com.example.hackathoneonebite

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.Data.User
import com.example.hackathoneonebite.api.LoginCheckEmailExistRequest
import com.example.hackathoneonebite.api.LoginCheckEmailExistResponse
import com.example.hackathoneonebite.api.Main1LoadPostRequest
import com.example.hackathoneonebite.api.Main1LoadPostResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityStartBinding
import com.example.hackathoneonebite.explanation.ExplanationActivity1
import com.example.hackathoneonebite.main.MainFrameActivity
import com.example.hackathoneonebite.main.fragment.Main1HomeFirstFragment
import com.example.hackathoneonebite.main.fragment.Main3PostingRelaySearchActivity
import com.example.hackathoneonebite.main.fragment.Main3PostingTimeActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class StartActivity : ComponentActivity() {
    var auth:FirebaseAuth?= null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var binding: ActivityStartBinding

    private lateinit var btnGoogle:Button
    private lateinit var btnEmail:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        btnGoogle = binding.btnGoogle

        auth = FirebaseAuth.getInstance()

        btnGoogle.setOnClickListener {
            googleLogin()
        }

    }

    private fun init() {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            // 'isFirstRun'을 false로 설정하여, 이 코드가 다시 실행되지 않도록 합니다.
            val editor = sharedPreferences.edit()
            editor.putBoolean("isFirstRun", false)
            editor.apply()
        }
    }

    private fun googleLogin(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account.idToken!!)
                    Log.e("TAG", "아이디 토큰 잘 넘어감")

                } catch (e: ApiException) {
                    Log.e("TAG", "Google sign-in failed", e)
                }
            } else {
                Log.e("TAG", "Result is not OK")
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth!!.currentUser
                    Log.d("TAG", "Signed in with Google: ${user?.displayName}")
                    //movePage(task.result?.user)

                    val account = GoogleSignIn.getLastSignedInAccount(this)
                    val email = account?.email
                    val familyName = account?.familyName
                    val givenName = account?.givenName
                    val displayName = account?.displayName
                    val photoUrl = account?.photoUrl

                    // Now you have access to the user's Gmail information
                    Log.d("TAG", "Email: $email")
                    Log.d("TAG", "Family Name: $familyName")
                    Log.d("TAG", "Given Name: $givenName")
                    Log.d("TAG", "Display Name: $displayName")
                    Log.d("TAG", "Photo URL: $photoUrl")

                    val request = LoginCheckEmailExistRequest(email!!)
                    checkEmailExist(request, account!!)
                } else {
                    Log.e("TAG", "Google sign-in failed", task.exception)
                }
            }
    }


    /*private fun signAndSignUp(email:String, pwd:String) {
        auth?.createUserWithEmailAndPassword(email, pwd)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) { // 회원가입 성공
                movePage(task.result?.user)
            } else { // 회원가입 실패
                signInEmail(email, pwd)
            }
        }
    }

    private fun signInEmail(email:String, pwd:String) {
        auth?.signInWithEmailAndPassword(email, pwd)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) { // 로그인 성공
                movePage(task.result?.user)
            } else { // 로그인 실패
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }*/



    private fun movePage(isExist: Boolean, account: GoogleSignInAccount, id: Long, userId: String) {
        if(isExist) {
            Log.d("LOGIN: ", "서버에 해당 계정이 존재합니다. 로그인합니다.")
            val nextIntent = Intent(this, MainFrameActivity::class.java)
            nextIntent.putExtra("id", id)
            nextIntent.putExtra("userId", userId)
            startActivity(nextIntent)
        } else {
            Log.d("LOGIN: ", "서버에 해당 계정이 존재하지 않습니다. 회원가입합니다.")
            val i = Intent(this, SignInActivity::class.java)
            val email = account?.email
            val familyName = account?.familyName
            val givenName = account?.givenName
            val displayName = account?.displayName

            i.putExtra("email", email)
            i.putExtra("displayName", displayName)
            onBackPressedDispatcher.onBackPressed()
            startActivity(i)
        }
    }



    private fun setBasicLogin() {
        val nextIntent = Intent(this, MainFrameActivity::class.java)
        startActivity(nextIntent)

    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        resultLauncher.launch(signInIntent)
    }

    fun checkEmailExist(request: LoginCheckEmailExistRequest, account: GoogleSignInAccount){
        val call = RetrofitBuilder.api.loginCheckEmailExistRequest(request)
        call.enqueue(object : Callback<LoginCheckEmailExistResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<LoginCheckEmailExistResponse>,
                response: Response<LoginCheckEmailExistResponse>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    Toast.makeText(this@StartActivity, "응답 성공", Toast.LENGTH_SHORT).show()
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    if(userResponse == null) {
                        Log.e("LOGIN","response가 null입니다.")
                        return
                    }
                    movePage(userResponse?.isExist!!, account, userResponse.id, userResponse.userId)
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

            override fun onFailure(call: Call<LoginCheckEmailExistResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Toast.makeText(this@StartActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
}