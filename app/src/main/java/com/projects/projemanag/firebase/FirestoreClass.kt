package com.projects.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.projects.projemanag.activities.*
import com.projects.projemanag.models.Board
import com.projects.projemanag.models.User
import com.projects.projemanag.utils.Constants

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity : SignUpActivity, userInfo : User){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnCompleteListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error writing document", e)

            }
    }

    fun addUpdateTaskList(activity : Activity, board: Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList
        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(taskListHashMap).addOnSuccessListener {
            if(activity is TaskListActivity) {
                activity.addUpdateTaskListSucces()
            }else if(activity is CardDetailsActivity){
                activity.addUpdateTaskListSucces()
            }
        }.addOnFailureListener { e ->
            if(activity is TaskListActivity){
                activity.hideProgressDialog()
            }else if(activity is CardDetailsActivity){
                activity.hideProgressDialog()
            }
            Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
        }
    }

    /**
     * A function to get the Board Details.
     */
    fun getBoardDetails(activity : TaskListActivity, documentId : String){
        mFireStore.collection(Constants.BOARDS).document(documentId).get().addOnSuccessListener { document ->
            Log.i( "-->${activity.javaClass.simpleName}::", document.toString())
            val board = document.toObject(Board::class.java)!!
            //obtenemos el identificador unico
            board.documentId = document.id
            //get board details
            activity.boardDetails(board)

        }.addOnFailureListener { e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error!! while creating a board",e)
        }
    }

    /**
     * A function to get the list of created boards from the database.
     */
    fun getBoardsList(activity : MainActivity){
        // The collection name for BOARDS
        mFireStore.collection(Constants.BOARDS)
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
            .get()// Will get the documents snapshots.
            .addOnSuccessListener { document ->
            // Here we get the list of boards in the form of documents.
            Log.i(" Documents->${activity.javaClass.simpleName}", document.documents.toString())
            // Here we have created a new instance for Boards ArrayList.
            val boardList : ArrayList<Board> = ArrayList()
            // A for loop as per the list of documents to convert them into Boards ArrayList.
            for(i in document.documents){
                val board = i.toObject(Board::class.java)!!
                board.documentId = i.id
                boardList.add(board)
            }
            // Here pass the result to the base activity.
            activity.populateBoardsListToUI(boardList)
        }.addOnFailureListener { e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error!! while creating a board",e)
        }
    }

    // TODO (Create a function for creating a board and making an entry in the database.)
    fun createBoard(activity : CreateBoardActivity, board : Board){
        mFireStore.collection(Constants.BOARDS)
            .document().set(board, SetOptions.merge()).addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board created successfully.")
                Toast.makeText(activity, "Board created successfully.", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    fun updateUserProfileData(activity : Activity, userHashMap : HashMap<String,Any>){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName, "Profile Data update succesfully!")
            Toast.makeText(activity,"Profile update succesfully",Toast.LENGTH_LONG ).show()
            when(activity){
                is MainActivity -> activity.tokenUpdateSucces()
                is MyProfileActivity -> activity.profileUpdateSucces()
            }
        }.addOnFailureListener {
            e ->
            when(activity){
                is MainActivity -> activity.hideProgressDialog()
                is MyProfileActivity -> activity.hideProgressDialog()
            }

            Log.e(activity.javaClass.simpleName,"Error while creating a board.",e)
            Toast.makeText(activity,"Error !!!",Toast.LENGTH_LONG ).show()
        }
    }


    //"fun loadUserData(activity : SignInActivity)" podriamos haber colocado asi pero vamos a ponerla mas general con "fun loadUserData(activity : Activity)" y con un when evaluamos mas abajo
    fun loadUserData(activity : Activity, readBoardsList : Boolean = false ){
        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserId()).get().addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                // TODO (Pass the result to base activity.)
                // START
                // Aquí hemos recibido la instancia del documento que se convierte en el objeto del modelo de datos de usuario.
                val loggedInUser = document.toObject(User::class.java)!!/*--> si no queremos usar el "!!" devemos hacer
                una verificacion que no sea nulo asi
                    if(loggedInUser != null){activity.signInSuccess(loggedInUser)}
                */
                Log.i("instancia ->", loggedInUser.toString())
                // TODO(Modify the parameter and check the instance of activity and send the success result to it.)
                // Here call a function of base activity for transferring the result to it.
                when(activity){
                    is SignInActivity -> activity.signInSuccess(loggedInUser)
                    is MainActivity -> activity.updateNavigationUserDetails(loggedInUser,readBoardsList)
                    is MyProfileActivity -> activity.setUserDataInUI(loggedInUser)
                }
            }.addOnFailureListener { e ->
                when(activity){
                    is SignInActivity -> activity.hideProgressDialog()
                    is MainActivity -> activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error while getting loggedIn user details", e)
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser//!!.uid <-- Podriamos haber agregado esto pero necesitamos saber si no es nulo
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getAssignedMembersListDetails(activity : Activity,assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                document ->
                    Log.i("${activity.javaClass.simpleName} ->",document.documents.toString())
                    val userList : ArrayList<User> = ArrayList()

                    for(i in document.documents){
                        val user = i.toObject(User::class.java)!!
                        userList.add(user)
                    }
                    if(activity is MembersActivity){
                        activity.setupMembersList(userList)
                    }else if(activity is TaskListActivity){
                        activity.boardMembersDetailsList(userList)
                    }

            }.addOnFailureListener { e ->
                if(activity is MembersActivity){
                    activity.hideProgressDialog()
                }else if(activity is TaskListActivity){
                    activity.hideProgressDialog()
                }

                Log.e(activity.javaClass.simpleName,"Error creando",e)
            }
    }

    fun getMemberDetails(activity : MembersActivity, email: String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener { document ->
                if(document.documents.size > 0){
                    //cuando se agrega un nuevo usuario siempre queda en la posicion 0
                    val user = document.documents[0].toObject(User::class.java)!!
                    Log.i("User!! -> ", user.toString())
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No Such member found")
                }
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while geting user details",e)
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User){
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSucces(user)
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName,"Error while creating a board.", e)
            }
    }
}