package com.kkm.mvvmkistagram.login

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kkm.mvvmkistagram.R

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    var auth = FirebaseAuth.getInstance()

    var id : MutableLiveData<String> = MutableLiveData("antmin1114")
    var password : MutableLiveData<String> = MutableLiveData("1234")

    var showInputNumberActivity : MutableLiveData<Boolean> = MutableLiveData(false)
    var showFindIdActivity : MutableLiveData<Boolean> = MutableLiveData(false)

    val context = getApplication<Application>().applicationContext

    var googleSigninClient : GoogleSignInClient

    init {

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSigninClient = GoogleSignIn.getClient(context, gso)
    }

    fun loginWithSignupEmail() {

        println("Email")
        auth.createUserWithEmailAndPassword(id.value.toString(), password.value.toString()).addOnCompleteListener {

            if (it.isSuccessful) {

                showInputNumberActivity.value = true

            } else {

                // 아이디가 있을 경우

            }
        }

    }

    fun loginGoogle(view : View) {

        var i = googleSigninClient.signInIntent
        (view.context as? LoginActivity)?.googleLoginResult?.launch(i)

    }

    fun firebaseAuthWithGoogle(idToken : String?) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {

            if (it.isSuccessful) {

                showInputNumberActivity.value = true

            } else {

                // 아이디가 있을 경우

            }

        }

    }
}