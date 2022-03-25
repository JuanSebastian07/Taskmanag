package com.projects.projemanag.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.projemanag.databinding.ItemLabelColorBinding

class LabelColorListItemsAdapter(private val context : Context, private var list : ArrayList<String>, private val mSelectedColor : String) : RecyclerView.Adapter<LabelColorListItemsAdapter.ViewHolder>() {

    var onClickListener : OnClickListener? = null

    inner class ViewHolder(binding : ItemLabelColorBinding) : RecyclerView.ViewHolder(binding?.root){
        val viewMain = binding?.viewMain
        val ivSelectedColor = binding?.ivSelectedColor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLabelColorBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        Log.i("Item->",item)
        holder.viewMain.setBackgroundColor(Color.parseColor(item))
        if(item == mSelectedColor){
            holder.ivSelectedColor.visibility = View.VISIBLE
        }else{
            holder.ivSelectedColor.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick(position, item)
            }
        }

    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int, item: String)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}