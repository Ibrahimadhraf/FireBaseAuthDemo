package com.ibrahim.firebasedemo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth=FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            registerUser()
        }
        btnLogin.setOnClickListener {
            logInUser()
        }
        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }
        btnGotoLoginWithGoogle.setOnClickListener {
            val intent = Intent(this ,LogInWithGoogle::class.java)
            startActivity(intent)
        }
    }
    private fun updateProfile(){
        auth.currentUser?.let {user->
            val username=etUsername.text.toString()
            val photoUri=Uri.parse("android.resource://$packageName/${R.drawable.test}")
            val profileUpdates=UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoUri)
                .build()
            CoroutineScope(Dispatchers.IO).launch{
                 try {
                   user.updateProfile(profileUpdates).await()
                     withContext(Dispatchers.Main){
                         checkLogInState()
                         Toast.makeText(this@MainActivity, "Successfully Update Profile", Toast.LENGTH_SHORT).show()
                     }
                 }catch (e:Exception){
                     withContext(Dispatchers.Main){
                         Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                     }
                 }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkLogInState()
    }
    private fun registerUser(){
        val email=etEmailRegister.text.toString()
        val password=etPasswordRegister.text.toString()
        if(email.isNotEmpty() &&password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                     auth.createUserWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        checkLogInState()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        Log.d("test" ,""+e.message)
                    }
                }
            }
        }
    }
    private fun logInUser(){
        val email=etEmailLogin.text.toString()
        val password=etPasswordLogin.text.toString()
        if(email.isNotEmpty() &&password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        checkLogInState()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        Log.d("test" ,""+e.message)
                    }
                }
            }
        }
    }

    private fun checkLogInState() {
        val user=auth.currentUser
        if(user==null){
             tvLoggedIn.text="You are not Logged in"
        }else{
            tvLoggedIn.text="You are Logged in"
            etUsername.setText(user.displayName)
            ivProfilePicture.setImageURI(user.photoUrl)
        }
    }
}