package com.projects.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.projemanag.R
import com.projects.projemanag.adapters.TaskListItemAdapter
import com.projects.projemanag.databinding.ActivityTaskListBinding
import com.projects.projemanag.firebase.FirestoreClass
import com.projects.projemanag.models.Board
import com.projects.projemanag.models.Card
import com.projects.projemanag.models.Task
import com.projects.projemanag.models.User
import com.projects.projemanag.utils.Constants

class TaskListActivity : BaseActivity() {

    private var binding : ActivityTaskListBinding? = null
    private lateinit var mBoardDetails : Board
    private lateinit var mBoardDocumentId: String
    lateinit var mAssignedMemberDetailList : ArrayList<User>

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == Activity.RESULT_OK){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this@TaskListActivity,mBoardDocumentId)
        }else{
            Log.e("Cancelled","Cancelled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDocumentId)
    }//onCreate

    private fun setupActionBar(){
        //val binding = AppBarMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setSupportActionBar(binding?.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }
        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun cardDetails(taskListPosition : Int, cardPosition : Int){
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBER_LIST, mAssignedMemberDetailList)
        launcher.launch(intent)
    }

    // TODO (Inflate the action menu for TaskListScreen and also launch the MembersActivity Screen on item selection.)
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * A function to get the result of Board Detail.
     */

    fun boardDetails(board: Board){
        mBoardDetails = board

        Log.i("Board -> :", board.toString())
        //Thread.sleep(5000)
        hideProgressDialog()

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assignedTo)

    }

    fun createTaskList(taskListName : String){
        val task = Task(taskListName, FirestoreClass().getCurrentUserId())
        Log.i("Board3 -> :", mBoardDetails.toString())
        mBoardDetails.taskList.add(0,task)
        Log.i("Board4 -> :", mBoardDetails.toString())
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        Log.i("Board5 -> :", mBoardDetails.toString())

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun addUpdateTaskListSucces(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDetails.documentId)
    }

    fun updateTaskList(position : Int, listName : String , model : Task){
        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        Log.i("taskupdate ->", (mBoardDetails.taskList[position]).toString())
        Log.i("taskupdate ->", task.toString())
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    fun updateCardsInTaskList(taskListPosition: Int, cards : ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        mBoardDetails.taskList[taskListPosition].cards = cards

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    /**
     * A function to create a card and update it in the task list.
     */
    fun addCardToTaskList(position : Int, cardName : String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUsersList : ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserId())

        val card = Card(cardName, FirestoreClass().getCurrentUserId(), cardAssignedUsersList)

        val cardsList = mBoardDetails.taskList[position].cards
        Log.i("cardsList-->", cardsList.toString())
        cardsList.add(card)

        val task = Task(mBoardDetails.taskList[position].title, mBoardDetails.taskList[position].createdBy, cardsList)
        mBoardDetails.taskList[position] = task

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun boardMembersDetailsList(list : ArrayList<User>){
        mAssignedMemberDetailList = list
        hideProgressDialog()

        val addTaskList = Task(resources.getString(R.string.add_list))
        //Al atributo tasklist  de la clase Board le agregamos el obj addTaskList de la clase Task
        mBoardDetails.taskList.add(addTaskList)

        Log.i("Board2 -> :", mBoardDetails.toString())

        binding?.rvTaskList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        //tamaño fijo
        binding?.rvTaskList?.setHasFixedSize(true)

        val adapter = TaskListItemAdapter(this,mBoardDetails.taskList)
        binding?.rvTaskList?.adapter = adapter
    }
}