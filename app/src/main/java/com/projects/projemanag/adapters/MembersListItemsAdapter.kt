package com.projects.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projects.projemanag.R
import com.projects.projemanag.databinding.ItemMemberBinding
import com.projects.projemanag.models.User
import com.projects.projemanag.utils.Constants

class MembersListItemsAdapter(private val context : Context, private var list : ArrayList<User>) : RecyclerView.Adapter<MembersListItemsAdapter.ViewHolder>() {

    private var onClickListener : OnClickListener? = null

    inner class ViewHolder(binding : ItemMemberBinding) : RecyclerView.ViewHolder(binding.root){
        val ivMemberImage = binding?.ivMemberImage
        val tvMemberEmail = binding?.tvMemberEmail
        val tvMemberName = binding?.tvMemberName
        val ivSelectedMember = binding?.ivSelectedMember
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(ItemMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MembersListItemsAdapter.ViewHolder, position: Int) {
        val model = list[position]
        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.ivMemberImage)

        holder.tvMemberName.text = model.name
        holder.tvMemberEmail.text = model.email

        if(model.selected){
            holder.ivSelectedMember.visibility = View.VISIBLE
        }else{
            holder.ivSelectedMember.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                if(model.selected){
                    onClickListener!!.onClick(position,model, Constants.UN_SELECT)
                }else{
                    onClickListener!!.onClick(position, model, Constants.SELECT)
                }
            }
        }
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int, model: User, action: String)
    }
}