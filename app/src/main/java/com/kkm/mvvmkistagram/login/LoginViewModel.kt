package com.kkm.mvvmkistagram.login

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kkm.mvvmkistagram.R
import java.util.*
import kotlin.math.log

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    var auth = FirebaseAuth.getInstance()

    var id : MutableLiveData<String> = MutableLiveData("antmin1114@naver.com")
    var password : MutableLiveData<String> = MutableLiveData("123456")

    var showInputNumberActivity : MutableLiveData<Boolean> = MutableLiveData(false)
    var showFindIdActivity : MutableLiveData<Boolean> = MutableLiveData(false)
    var showMainActivity : MutableLiveData<Boolean> = MutableLiveData(false)
    var showToastMessage : MutableLiveData<String> = MutableLiveData()

    val context = getApplication<Application>().applicationContext
    var googleSignInClient : GoogleSignInClient

    init {

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)

    }

    fun loginWithSignupEmail() {

        println("Email")
        auth.createUserWithEmailAndPassword(id.value.toString(), password.value.toString()).addOnCompleteListener {

            if (it.isSuccessful) {

                showInputNumberActivity.value = true

            } else {

                // 아이디가 있을 경우
                loginEmail()

            }
        }

    }

    private fun loginEmail() {

        auth.signInWithEmailAndPassword(id.value.toString(), password.value.toString()).addOnCompleteListener {
            Log.d("success-result", "id: ${id.value.toString()}\npassword: ${password.value.toString()}")

            if (it.isSuccessful) {

                if (it.result.user?.isEmailVerified == true) {

                    showMainActivity.value = true

                } else {

                    showToastMessage.value = "이메일이 인증되지 않았습니다."

                }

            }
        }.addOnFailureListener {

            Log.e("success-result", "loginEmail: 오류내용: $it" )
            if (it.toString().contains("The password is invalid")) {

                showToastMessage.value = "비밀번호가 올바르지 않습니다."

            } else {

                showToastMessage.value = "이메일이 인증되지 않았습니다."

            }

        }

    }

    fun loginGoogle(view : View) {

        var i = googleSignInClient.signInIntent
        (view.context as? LoginActivity)?.googleLoginResult?.launch(i)

    }

    fun firebaseAuthWithGoogle(idToken : String?) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {

                if (it.result.user?.isEmailVerified == true) {
                    showMainActivity.value = true
                } else {
                    showInputNumberActivity.value = true
                }

            }
        }.addOnFailureListener {

            Log.e("login-error", "firebaseAuthWithGoogle: $it")

            showToastMessage.value = "동일 이메일이므로 같은 계정으로 접속합니다."
            auth.currentUser!!.linkWithCredential(credential).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result.user?.isEmailVerified == true) {
                        showMainActivity.value = true
                    } else {
                        showInputNumberActivity.value = true
                    }

                }

            }

        }


    }

    fun firebaseAuthWithFacebook(accessToken: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential).addOnCompleteListener {

            if (it.isSuccessful) {
                Log.d("success-result", "firebaseAuthWithFacebook: ${auth.currentUser?.email}")

                if (it.result.user?.isEmailVerified == true) {
                    showMainActivity.value = true
                } else {
                    showInputNumberActivity.value = true
                }

            }
        }.addOnFailureListener {

            Log.e("login-error", "firebaseAuthWithFacebook: $it")
            auth.currentUser!!.linkWithCredential(credential).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if (task.result.user?.isEmailVerified == true) {
                        showMainActivity.value = true
                    } else {
                        showInputNumberActivity.value = true
                    }
                }
            }

        }

    }
}