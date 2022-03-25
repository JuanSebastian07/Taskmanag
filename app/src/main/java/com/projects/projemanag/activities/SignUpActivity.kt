package com.projects.projemanag.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.projects.projemanag.R
import com.projects.projemanag.databinding.ActivitySignUpBinding
import com.projects.projemanag.firebase.FirestoreClass
import com.projects.projemanag.models.User

// TODO Extend the BaseActivity instead of AppCompatActivity.
class SignUpActivity : BaseActivity() {

    private var binding : ActivitySignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnSignUp?.setOnClickListener {
            registerUsuario()
        }
    }

    private fun registerUsuario(){
        //con trim removemos espacios vacios o algun caracter en especial que el usuario escribio si deseamos
        val name : String = binding?.etName?.text.toString().trim{ it <= ' '}
        val email : String = binding?.etEmail?.text.toString().trim{ it <= ' '}
        val password : String = binding?.etPassword?.text.toString().trim{ it <= ' '}

        if(validateForm(name,email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    // Firebase registered user
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    // Registered Email
                    val registeredEmail = firebaseUser.email!!
                    /**/
                    // TODO(As you can see we are now authenticated by Firebase but for more inserting more details we need to use the DATABASE in Firebase.)
                    // START
                    // Before start with database we need to perform some steps in Firebase Console and add a dependency in Gradle file.
                    // Follow the Steps:
                    // Step 1: Go to the "Database" tab in the Firebase Console in your project details in the navigation bar under "Develop".
                    // Step 2: In the Database Page and Click on the Create Database in the Cloud Firestore in the test mode. Click on Next
                    // Step 3: Select the Cloud Firestore location and press the Done.
                    // Step 4: Now the database is created in the test mode and now add the cloud firestore dependency.
                    // Step 5: For more details visit the link: https://firebase.google.com/docs/firestore
                    // END

                    // TODO (Now here we will make an entry in the Database of a new user registered.)
                    // START
                    //instancia de la clase User
                    val user = User(firebaseUser.uid, name, registeredEmail,password)
                    // call the registerUser function of FirestoreClass to make an entry in the database.
                    FirestoreClass().registerUser(this@SignUpActivity, user)
                    
                    // END
                    /**/

                    /*
                    /**
                     * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
                     * and send him to Intro Screen for Sign-In
                     */
                    FirebaseAuth.getInstance().signOut()
                    // Finish the Sign-Up Screen
                    finish()
                    */
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateForm(name : String, email : String, password : String) : Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                //Heradamos metodo de la clase padre
                showErrorSnackBar("Please enter a name")
                false
            }TextUtils.isEmpty(email) -> {
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

    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun userRegisteredSuccess() {
        Toast.makeText(this@SignUpActivity, "You have successfully registered.", Toast.LENGTH_LONG).show()
        hideProgressDialog()
        //Thread.sleep(5000)
        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Sign-Up Screen
        finish()
    }
}