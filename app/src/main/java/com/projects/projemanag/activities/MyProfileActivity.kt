package com.projects.projemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.projects.projemanag.R
import com.projects.projemanag.databinding.ActivityMyProfileBinding
import com.projects.projemanag.firebase.FirestoreClass
import com.projects.projemanag.models.User
import com.projects.projemanag.utils.Constants
import com.projects.projemanag.utils.getFileExtension
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var binding : ActivityMyProfileBinding? = null
    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails : User
    private var mProfileImageURL : String = ""

    private val openGalleryLauncher : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
                if(result.resultCode == RESULT_OK && result.data != null){
                    //process the data
                    mSelectedImageFileUri = result.data?.data
                    //Aqui fijamos la imagen que seleccionamos en la galeria ->
                    //binding?.ivProfileUserImage?.setImageURI(result.data?.data)
                    try {
                        // Load the user image in the ImageView.
                        binding?.ivProfileUserImage?.let {
                            Glide
                                .with(this@MyProfileActivity)
                                .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                                .centerCrop() // Scale type of the image.
                                .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                                .into(it)
                        } // the view in which the image will be loaded.
                    }catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
        }
    private val requestPermission : ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        permission->permission.entries.forEach{
            val permissionName = it.key
            val isGranted = it.value

            if(isGranted){
                Toast.makeText(this, "Permission granted now you can read the storage files.", Toast.LENGTH_LONG ).show()
                //perform operation
                //Todo create an intent to pick image from external storage
                val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //Todo using the intent launcher created above launch the pick intent
                openGalleryLauncher.launch(pickIntent)
            }else{
                if(permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                    Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG ).show()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        binding?.ivProfileUserImage?.setOnClickListener {
            requestStoragePermission()
        }

        binding?.btnUpdate?.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }//onCreate

    private fun setupActionBar(){
        //val binding = AppBarMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setSupportActionBar(binding?.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setUserDataInUI(user : User){
        mUserDetails = user
        binding?.ivProfileUserImage?.let {
            Glide
                .with(this@MyProfileActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(it)

            binding?.etName?.setText(user.name)
            binding?.etEmail?.setText(user.email)
            if(user.mobile != 0L){
                binding?.etMobile?.setText(user.mobile.toString())
            }
        }
    }

    //create a method to requestStorage permission
    private fun requestStoragePermission(){
        // Check if the permission was denied and show rationale
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //call the rationale dialog to tell the user why they need to allow permission request
            showRationaleDialog("Projemanag","Projemanag " + "needs to Access Your External Storage")
        }
        else {
            // You can directly ask for the permission.
            //if it has not been denied then request for permission
            //  The registered ActivityResultCallback gets the result of this request.
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }

    }
    /**  create rationale dialog
     * Shows rationale dialog for displaying why the app needs permission
     * Only shown if the user has denied the permission request previously
     */
    private fun showRationaleDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title).setMessage(message).setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    /**
     * A function to update the user profile details into the database.
     */
    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()
        //var anyChangesMade = false

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){
            //userHashMap["image"]
            userHashMap[Constants.IMAGE] = mProfileImageURL
            //anyChangesMade = true
        }
        if(binding?.etName?.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = binding?.etName?.text.toString()
            //anyChangesMade = true
        }
        if(binding?.etMobile?.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = binding?.etMobile?.text.toString().toLong()
            //anyChangesMade = true
        }

        //if(anyChangesMade){
        //Thread.sleep(2000)
        FirestoreClass().updateUserProfileData(this, userHashMap)
        //}
    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri != null){                                                                                                          //Aqui podremos acceder de dos formas al metodo getFileExtension que esta en Constant, una seria Constant.getFileExtension y la otra importando el metodo y asi solo llamamos a getFileExtension como lo hicimos y asi ya no tendriamos que cambiar en todas partes donde hacemos uso de ese metodo
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child("USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(this,mSelectedImageFileUri))
            Log.i("sRef -->", sRef.toString())
            //Aqui ya ponemos la img en la base de datos
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { TaskSnapshot ->
                Log.i("Firebase Image URL",TaskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                TaskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageURL = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this,exception.message , Toast.LENGTH_LONG ).show()
                hideProgressDialog()
            }
        }
    }
    /* Estamos la pasamos a constants para poder acceder desde otros metodos a ella pero debemos agregarle
    el atributo activity: Activity, esto para saber con cual activity deberia obtener la uri
    private fun getFileExtension(uri : Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }*/

    fun profileUpdateSucces(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        //cerramos activity
        finish()
    }
}