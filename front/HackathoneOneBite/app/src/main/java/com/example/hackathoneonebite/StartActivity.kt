package com.example.hackathoneonebite

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
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
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.Data.User
import com.example.hackathoneonebite.api.Main1LoadPostRequest
import com.example.hackathoneonebite.api.Main1LoadPostResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityStartBinding
import com.example.hackathoneonebite.main.MainFrameActivity
import com.example.hackathoneonebite.main.fragment.Main1HomeFirstFragment
import com.example.hackathoneonebite.main.fragment.Main3PostingTimeActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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

        //setResultSignUp()
        //init()

        btnEmail = binding.btmEmail
        btnGoogle = binding.btnGoogle

        auth = FirebaseAuth.getInstance()
        btnEmail.setOnClickListener {
            val strEmail = binding.userID.toString()
            val strPwd = binding.userPW.toString()
            try{
                signAndSignUp(strEmail, strPwd)
            } catch (e:java.lang.Exception) {
                Toast.makeText(this, "아이디와 비밀번호를 제대로 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoogle.setOnClickListener {
            googleLogin()
        }

        binding.loginBtn.setOnClickListener {
            setBasicLogin()
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
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth!!.currentUser
                    Log.d("TAG", "Signed in with Google: ${user?.displayName}")
                    movePage(task.result?.user)

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
                } else {
                    Log.e("TAG", "Google sign-in failed", task.exception)
                }
            }
    }


    private fun signAndSignUp(email:String, pwd:String) {
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
    }



    private fun movePage(user:FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainFrameActivity::class.java))
        }
    }



    private fun setBasicLogin() {
        val nextIntent = Intent(this, MainFrameActivity::class.java)
        startActivity(nextIntent)

    }

    /*override fun onStart() {
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

    private fun setGoogleLogin(auth: FirebaseAuth) {



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
    }*/

    @SuppressLint("ResourceType")
    fun init() {
        binding.singInBtn.setOnClickListener {
            val inputStream = this.resources.openRawResource(R.drawable.cd_outer_playing)
            val byteArray = inputStream.readBytes()
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), byteArray)
            val imagePart = MultipartBody.Part.createFormData("image", "your_image_name.jpg", requestFile)

            val themePart = RequestBody.create("text/plain".toMediaTypeOrNull(), "1")

            Login(imagePart, themePart)
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        resultLauncher.launch(signInIntent)
    }

    fun Login(image: MultipartBody.Part, theme: RequestBody){
        val call = RetrofitBuilder.api.main1LoadPost(image, theme)
        call.enqueue(object : Callback<Main1LoadPostResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<Main1LoadPostResponse>,
                response: Response<Main1LoadPostResponse>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    Log.d("RESPONSE: ", "ID: ${userResponse?.text}, Name: ${userResponse?.date.toString()}")
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

            override fun onFailure(call: Call<Main1LoadPostResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
}
