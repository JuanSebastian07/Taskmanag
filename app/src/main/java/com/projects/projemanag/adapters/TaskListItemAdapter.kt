package com.projects.projemanag.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projects.projemanag.activities.TaskListActivity
import com.projects.projemanag.databinding.ItemTaskBinding
import com.projects.projemanag.models.Task
import java.util.*
import kotlin.collections.ArrayList


class TaskListItemAdapter(private val context: Context, private val list: ArrayList<Task>):
    RecyclerView.Adapter<TaskListItemAdapter.ViewHolder>(){

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    inner class ViewHolder(binding : ItemTaskBinding):RecyclerView.ViewHolder(binding.root) {
        val tvAddTaskList = binding?.tvAddTaskList
        val llTaskItem = binding?.llTaskItem
        val tvTaskListTitle = binding?.tvTaskListTitle
        val cvAddTaskListName = binding?.cvAddTaskListName
        val ibCloseListName = binding?.ibCloseListName
        val ibDoneListName = binding.ibDoneListName
        val etTaskListName = binding?.etTaskListName
        val ibEditListName = binding?.ibEditListName
        val etEditTaskListName = binding?.etEditTaskListName
        val llTitleView = binding?.llTitleView
        val cvEditTaskListName = binding?.cvEditTaskListName
        val ibCloseEditableView = binding?.ibCloseEditableView
        val ibDoneEditListName = binding?.ibDoneEditListName
        val ibDeleteList = binding?.ibDeleteList
        val tvAddCard = binding?.tvAddCard
        val cvAddCard = binding?.cvAddCard
        val ibCloseCardName = binding?.ibCloseCardName
        val ibDoneCardName = binding?.ibDoneCardName
        val etCardName = binding?.etCardName
        val rvCardList = binding?.rvCardList
    }

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view = ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        val layoutParams = LinearLayout.LayoutParams((parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        // Here the dynamic margins are applied to the view.
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.root.layoutParams = layoutParams

        return ViewHolder(view)
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list[position]

        if(position == list.size - 1){
            holder.tvAddTaskList.visibility = View.VISIBLE
            holder.llTaskItem.visibility = View.GONE
        }else{
            holder.tvAddTaskList.visibility = View.GONE
            holder.llTaskItem.visibility = View.VISIBLE
        }

        // TODO (Add a click event for showing the view for adding the task list name. And also set the task list title name.)
        holder.tvTaskListTitle.text = model.title

        holder.tvAddTaskList.setOnClickListener {
            holder.tvAddTaskList.visibility = View.GONE
            holder.cvAddTaskListName.visibility = View.VISIBLE
        }

        // TODO (Add a click event for hiding the view for adding the task list name.)
        holder.ibCloseListName.setOnClickListener {
            holder.tvAddTaskList.visibility = View.VISIBLE
            holder.cvAddTaskListName.visibility = View.GONE
        }

        // TODO (Add a click event for passing the task list name to the base activity function. To create a task list.)
        holder.ibDoneListName.setOnClickListener {
            val listName = holder.etTaskListName.text.toString()

            if(listName.isNotEmpty()){
                // Here we check the context is an instance of the TaskListActivity.
                if(context is TaskListActivity){
                    context.createTaskList(listName)
                }
            }else{
                Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
            }
        }

        holder.ibEditListName.setOnClickListener {
            holder.etEditTaskListName.setText(model.title)
            holder.llTitleView.visibility = View.GONE
            holder.cvEditTaskListName.visibility = View.VISIBLE
        }

        holder.ibCloseEditableView.setOnClickListener {
            holder.llTitleView.visibility = View.VISIBLE
            holder.cvEditTaskListName.visibility = View.GONE
        }

        holder.ibDoneEditListName.setOnClickListener {
            val listName = holder.etEditTaskListName.text.toString()

            if(listName.isNotEmpty()){
                // Here we check the context is an instance of the TaskListActivity.
                if(context is TaskListActivity){
                    context.updateTaskList(position,listName, model)
                }
            }else{
                Toast.makeText(context, "Please Enter a List Name.", Toast.LENGTH_SHORT).show()
            }
        }

        holder.ibDeleteList.setOnClickListener {
            alertDialogForDeleteList(position, model.title)
        }

        holder.tvAddCard.setOnClickListener {
            holder.tvAddCard.visibility = View.GONE
            holder.cvAddCard.visibility =  View.VISIBLE
        }

        holder.ibCloseCardName.setOnClickListener {
            holder.tvAddCard.visibility = View.VISIBLE
            holder.cvAddCard.visibility = View.GONE
        }

        holder.ibDoneCardName.setOnClickListener {
            val cardName = holder.etCardName.text.toString()

            if(cardName.isNotEmpty()){
                // Here we check the context is an instance of the TaskListActivity.
                if(context is TaskListActivity){
                    context.addCardToTaskList(position, cardName)
                }
            }else{
                Toast.makeText(context, "Please Enter Card Name.", Toast.LENGTH_SHORT).show()
            }
        }

        holder.rvCardList.layoutManager = LinearLayoutManager(context)
        holder.rvCardList.setHasFixedSize(true)

        val adapter = CardListItemAdapter(context, model.cards)
        holder.rvCardList.adapter = adapter

        adapter.setOnClickListener(object : CardListItemAdapter.OnClickListener {
            override fun onClick(cardPosition: Int) {
                if (context is TaskListActivity){
                    context.cardDetails(position, cardPosition)
                }
            }
        })

        //Adding The Drag And Drop Feature
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        holder.rvCardList.addItemDecoration(dividerItemDecoration)

        //  Creates an ItemTouchHelper that will work with the given Callback.
        val helper = ItemTouchHelper(object :
        ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0 ){
            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val draggedPosition = dragged.adapterPosition
                val targetPosition = target.adapterPosition

                if (mPositionDraggedFrom == -1){
                    mPositionDraggedFrom == draggedPosition
                }
                mPositionDraggedTo = targetPosition
                Collections.swap(list[position].cards, draggedPosition, targetPosition)
                adapter.notifyItemMoved(draggedPosition, targetPosition)
                return false // true if moved, false otherwise
            }

            // Called when a ViewHolder is swiped by the user.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // remove from adapter
            }

            // TODO (Finally when the dragging is completed than call the function to update the cards in the database and reset the global variables.)
            /*Called by the ItemTouchHelper when the user interaction with an element is over and it
             also completed its animation.*/
            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                if(mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo){
                    (context as TaskListActivity).updateCardsInTaskList(position, list[position].cards)
                }
                mPositionDraggedFrom = -1
                mPositionDraggedTo = -1
            }

        })
        helper.attachToRecyclerView(holder.rvCardList)
    }

    /**
     * Method is used to show the Alert Dialog for deleting the task list.
     */
    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function to get density pixel from pixel
     */
    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    /**
     * A function to get pixel from density pixel
     */
    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

}