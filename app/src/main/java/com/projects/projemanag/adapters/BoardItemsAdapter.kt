package com.projects.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projects.projemanag.R
import com.projects.projemanag.databinding.ItemBoardBinding
import com.projects.projemanag.models.Board


class BoardItemsAdapter(private val context: Context, private val list: ArrayList<Board>):RecyclerView.Adapter<BoardItemsAdapter.ViewHolder>() {

    private var onClicklistener : OnItemClickListener? = null

    inner class ViewHolder(binding: ItemBoardBinding):RecyclerView.ViewHolder(binding.root){
        val tvName = binding?.tvName
        val ivBoardImage = binding?.ivBoardImage
        val tvCreatedBy = binding?.tvCreatedBy
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        //lo que queremos que aparezca en el recyclerView lo colocamos aqui
        /*
        holder.tvName.text = item.name
        holder.ivBoardImage.setImageURI(Uri.parse(item.image))
        holder.tvCreatedBy.text = "Created By " + item.createdBy*/

        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(holder.ivBoardImage)

        holder.tvName.text = model.name
        holder.tvCreatedBy.text = "Created By : ${model.createdBy}"

        holder.itemView.setOnClickListener {
            if (onClicklistener != null) {
                onClicklistener!!.onItemClick(position, model)
            }
        }

    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnItemClickListener) {
        this.onClicklistener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int, model: Board)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}


