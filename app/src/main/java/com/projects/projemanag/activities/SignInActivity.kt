package com.projects.projemanag.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.projects.projemanag.R
import com.projects.projemanag.databinding.ActivitySignInBinding
import com.projects.projemanag.firebase.FirestoreClass
import com.projects.projemanag.models.User

// TODO Extend the BaseActivity instead of AppCompatActivity.)
class SignInActivity : BaseActivity() {

    private lateinit var auth : FirebaseAuth
    private var binding : ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //auth = FirebaseAuth.getInstance()

        setupActionBar()

        binding?.btnSignIn?.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignInActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarSignInActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun signInRegisteredUser(){
        val email : String = binding?.etEmail?.text.toString().trim{ it <= ' '}
        val password : String = binding?.etPassword?.text.toString().trim{ it <= ' '}

        if (validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            // Sign-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirestoreClass().loadUserData(this@SignInActivity)
                    } else {
                        Toast.makeText(this@SignInActivity, task.exception!!.message, Toast.LENGTH_LONG).show()
                        hideProgressDialog()
                    }
                }
        }
    }

    private fun validateForm(email : String, password : String) : Boolean{
        return when{
            TextUtils.isEmpty(email) -> {
                //Heradamos metodo de la clase padre
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                //Heradamos metodo de la clase padre
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

    /*
    La otra forma sin el when
    * A function to validate the entries of a user.
    private fun validateForm(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }
    }
    */

    /**
     * A function to get the user details from the firestore database after authentication.
     */
    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }
}