package com.kkm.mvvmkistagram.login

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.kkm.mvvmkistagram.MainActivity
import com.kkm.mvvmkistagram.R
import com.kkm.mvvmkistagram.databinding.ActivityLoginBinding
import java.util.Arrays


class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    lateinit var callbackManager: CallbackManager
    val TAG = "LoginActivity-facebook"
    val loginViewModel : LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = loginViewModel
        binding.activity = this
        binding.lifecycleOwner = this
        callbackManager = CallbackManager.Factory.create()
        setObserve()

    }

    fun loginFacebook() {

        var loginManager = LoginManager.getInstance()
        loginManager.loginBehavior = LoginBehavior.WEB_ONLY
        loginManager.logInWithReadPermissions(this, Arrays.asList("email"))
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {

                Log.d(TAG, "onCancel: 취소됌")

            }

            override fun onError(error: FacebookException) {

                Log.e(TAG, "onError: ${error.message}")

            }

            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "onSuccess: ${result.accessToken}\n${loginViewModel.auth.currentUser}")
                val token = result.accessToken
                loginViewModel.firebaseAuthWithFacebook(token)

            }

        })

    }

    fun setObserve() {

        loginViewModel.showInputNumberActivity.observe(this) {

            if (it) {

                startActivity(Intent(this, InputNumberActivity::class.java))

            }

        }

        loginViewModel.showFindIdActivity.observe(this) {

            if (it) {

                startActivity(Intent(this, FindIdActivity::class.java))

            }

        }

        loginViewModel.showMainActivity.observe(this) {

            if (it) {
                startActivity(Intent(this, MainActivity::class.java))
            }

        }

        loginViewModel.showToastMessage.observe(this) {

            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()

        }

    }

    fun findId() {

        println("findId")
        loginViewModel.showFindIdActivity.value = true

    }

    // 구글로그인이 성공한 결과값 받는 함수
    var googleLoginResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        result ->

        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java)
        account.idToken // 로그인한 사용자 정보를 암호화한 값
        loginViewModel.firebaseAuthWithGoogle(account.idToken)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)



    }

    /*    fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager.getPackageInfo(
                pContext.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }*/

}