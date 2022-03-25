package com.projects.projemanag.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projects.projemanag.activities.TaskListActivity
import com.projects.projemanag.databinding.ItemCardBinding
import com.projects.projemanag.models.Card
import com.projects.projemanag.models.SelectedMembers

class CardListItemAdapter(private val context: Context, private var list : ArrayList<Card>) : RecyclerView.Adapter<CardListItemAdapter.ViewHolder>() {

    private var onClickListener : OnClickListener? = null

    inner class ViewHolder(binding : ItemCardBinding) : RecyclerView.ViewHolder(binding.root){
        val tvCardName = binding?.tvCardName
        val viewLabelColor = binding?.viewLabelColor
        val rvCardSelectedMembersList = binding?.rvCardSelectedMembersList

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        Log.i("model -> ", model.toString())

        if(model.labelColor.isNotEmpty()){
            holder.viewLabelColor.visibility = View.VISIBLE
            holder.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
        }else{
            holder.viewLabelColor.visibility = View.GONE
        }
        holder.tvCardName.text = model.name

        if((context as TaskListActivity).mAssignedMemberDetailList.size >0){
            val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

            for(i in context.mAssignedMemberDetailList.indices){
                for(j in model.assignedTo){
                    if(context.mAssignedMemberDetailList[i].id == j ){
                        val selectedMembers = SelectedMembers(context.mAssignedMemberDetailList[i].id,
                            context.mAssignedMemberDetailList[i].image
                        )
                        selectedMembersList.add(selectedMembers)
                    }
                }
            }

            if(selectedMembersList.size > 0){
                if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                    holder.rvCardSelectedMembersList.visibility = View.GONE
                }else{
                    holder.rvCardSelectedMembersList.visibility = View.VISIBLE
                    holder.rvCardSelectedMembersList.layoutManager = GridLayoutManager(context, 4)

                    val adapter = CardMemberListItemAdapter(context, selectedMembersList,false)

                    holder.rvCardSelectedMembersList.adapter = adapter

                    adapter.setOnClickListener(object : CardMemberListItemAdapter.OnClickListener{
                        override fun onClick() {
                            if(onClickListener != null){
                                onClickListener!!.onClick(position)
                            }
                        }
                    })
                }
            }else{
                holder.rvCardSelectedMembersList.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}