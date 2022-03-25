package com.projects.projemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
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
import com.projects.projemanag.databinding.ActivityCreateBoardBinding
import com.projects.projemanag.firebase.FirestoreClass
import com.projects.projemanag.models.Board
import com.projects.projemanag.utils.Constants
import com.projects.projemanag.utils.getFileExtension
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var binding : ActivityCreateBoardBinding? = null
    //Url de la imagen
    private var mSelectedImageFileUri : Uri? = null
    //El valor que vamos a rescatar de la MainActivity
    private lateinit var mUserName : String
    //Url del tablero
    private var mBoardImageUrl: String = ""


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
                    binding?.ivBoardImage?.let {
                        Glide
                            .with(this)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        //aqui evaluamos si estamos recibiendo MainActivity un dato con la llave "name"
        if(intent.hasExtra(Constants.NAME)){
            //si es asi obtenemos el valor con la llave "name" y la guardamos en nuestra variable
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        binding?.ivBoardImage?.setOnClickListener {
            requestStoragePermission()
        }

        binding?.btnCreate?.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createdBoard()
            }
        }

    }//onCreate

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

    private val requestPermission : ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){
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

    private fun createdBoard(){
        val assignedUsersArrayList : ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())
        //instancia de la clase Board
        var board = Board(binding?.etBoardName?.text.toString(),mBoardImageUrl,mUserName,assignedUsersArrayList)
        FirestoreClass().createBoard(this,board)
    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        val sRef : StorageReference = FirebaseStorage.getInstance().reference.child("BOARD_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(this,mSelectedImageFileUri))
        Log.i("sRef -->", sRef.toString())
        //Aqui ya ponemos la img en la base de datos
        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { TaskSnapshot ->
                Log.i("Board Image URL",TaskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                TaskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.i("Downloadable Image URL",uri.toString())
                     mBoardImageUrl = uri.toString()
                     createdBoard()
                }
        }.addOnFailureListener { exception ->
            Toast.makeText(this,exception.message , Toast.LENGTH_LONG ).show()
            hideProgressDialog()
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar(){
        //val binding = AppBarMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener { onBackPressed() }
    }
}