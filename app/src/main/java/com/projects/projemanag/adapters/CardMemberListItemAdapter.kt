package com.projects.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projects.projemanag.R
import com.projects.projemanag.databinding.ItemCardSelectedMemberBinding
import com.projects.projemanag.models.SelectedMembers

class CardMemberListItemAdapter(private val context : Context, private val list : ArrayList<SelectedMembers>, private val assignMember : Boolean) : RecyclerView.Adapter<CardMemberListItemAdapter.ViewHolder>() {

    private var onClickListener : OnClickListener? = null

    inner class ViewHolder(binding : ItemCardSelectedMemberBinding) : RecyclerView.ViewHolder(binding.root){
        val ivAddMember = binding?.ivAddMember
        val ivSelectedMemberImage = binding?.ivSelectedMemberImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardMemberListItemAdapter.ViewHolder {
        return ViewHolder(ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CardMemberListItemAdapter.ViewHolder, position: Int) {
        val model = list[position]
        if(position == list.size - 1 && assignMember){
            holder.ivAddMember.visibility = View.VISIBLE
            holder.ivSelectedMemberImage.visibility = View.GONE
        }else{
            holder.ivAddMember.visibility = View.GONE
            holder.ivSelectedMemberImage.visibility = View.VISIBLE

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.ivSelectedMemberImage)
        }

        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
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
        fun onClick()
    }

}