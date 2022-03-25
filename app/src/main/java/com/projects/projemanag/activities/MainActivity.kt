package com.projects.projemanag.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.projects.projemanag.R
import com.projects.projemanag.adapters.BoardItemsAdapter
import com.projects.projemanag.databinding.ActivityMainBinding
import com.projects.projemanag.databinding.NavHeaderMainBinding
import com.projects.projemanag.firebase.FirestoreClass
import com.projects.projemanag.models.Board
import com.projects.projemanag.models.User
import com.projects.projemanag.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding : ActivityMainBinding? = null
    private var resultMyProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if(result.resultCode == RESULT_OK){
                FirestoreClass().loadUserData(this)
            }else{
                Log.e("Cancelled","Cancelled")
            }
    }

    private var resultBoardLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if(result.resultCode == RESULT_OK){
                FirestoreClass().getBoardsList(this)
            }else{

            }
    }

    private lateinit var mUserName : String
    private lateinit var mSharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        // TODO (Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.)
        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        binding?.navView?.setNavigationItemSelectedListener(this)

        //Initialize the mSharedPreferences variable
        mSharedPreferences = this.getSharedPreferences(Constants.PROJEMANAG_PREFERENCES, MODE_PRIVATE)

        // Variable is used get the value either token is updated in the database or not.
        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATE,false)

        // Here if the token is already updated than we don't need to update it every time.
        if (tokenUpdated){
            // Get the current logged in user details.
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this,true)
        }else{
            FirebaseMessaging.getInstance().token.addOnSuccessListener(this) { updateFCMToken(it)
                //identificador unico de nuestro dispositivo
                Log.i("Token ->", it)
            }
        }

        FirestoreClass().loadUserData(this, true)


        binding?.included?.fabCreateBoard?.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
            //le mandamos datos llave, valor, la llave para rescatar el valor que mandamos en la otra activity
            intent.putExtra(Constants.NAME,mUserName)
            resultBoardLauncher.launch(intent)
        }


    }//onCreate

    fun populateBoardsListToUI(boardList : ArrayList<Board>){
        hideProgressDialog()
        binding?.included?.fabCreateBoard?.visibility = View.VISIBLE
        if(boardList.size > 0){
            binding?.included?.included2?.rvBoardsList?.visibility = View.VISIBLE
            binding?.included?.included2?.tvNoBoardsAvailable?.visibility = View.GONE

            binding?.included?.included2?.rvBoardsList?.layoutManager = LinearLayoutManager(this)
            binding?.included?.included2?.rvBoardsList?.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this,boardList)
            binding?.included?.included2?.rvBoardsList?.adapter = adapter

            adapter.setOnClickListener(object : BoardItemsAdapter.OnItemClickListener{
                override fun onItemClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })

        }else{
            binding?.included?.included2?.rvBoardsList?.visibility = View.GONE
            binding?.included?.included2?.tvNoBoardsAvailable?.visibility = View.VISIBLE
        }
    }

    private fun setupActionBar(){
        //val binding = AppBarMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setSupportActionBar(binding?.included?.toolbarMainActivity)
        binding?.included?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        binding?.included?.toolbarMainActivity?.setNavigationOnClickListener {
            //Toggle drawer
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    // TODO (Add a onBackPressed function and check if the navigation drawer is open or closed.)
    override fun onBackPressed() {
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    /**
     * A function to get the current user details from firebase.
     */
    fun updateNavigationUserDetails(user : User, readBoardList: Boolean){
        hideProgressDialog()
        mUserName = user.name

        // The instance of the header view of the navigation view.
        val viewHeader = binding?.navView?.getHeaderView(0)
        val headerBinding = viewHeader?.let { NavHeaderMainBinding.bind(it) }
        headerBinding?.navUserImage?.let {
            Glide
                .with(this)
                .load(user.image) // URL of the image
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                .into(it)
        } // the view in which the image will be loaded.

        headerBinding?.tvUsername?.text = user.name

        if(readBoardList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        // TODO (Add the click events of navigation menu items.)
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                val intent = Intent(this@MainActivity, MyProfileActivity::class.java)
                resultMyProfileLauncher.launch(intent)
            }
            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                //Clear the shared preferences when the user signOut
                mSharedPreferences.edit().clear().apply()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * A function to notify the token is updated successfully in the database.
     */
    fun tokenUpdateSucces(){
        hideProgressDialog()

        // Here we have added a another value in shared preference that the token is updated in the database successfully.
        // So we don't need to update it every time.
        val editor : SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATE, true)
        editor.apply()

        // Get the current logged in user details.
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this,true)
    }

    /**
     * A function to update the user's FCM token into the database.
     */
    private fun updateFCMToken(token: String) {
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }
}