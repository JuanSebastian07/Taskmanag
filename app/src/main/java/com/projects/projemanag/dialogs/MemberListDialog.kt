package com.projects.projemanag.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.projemanag.adapters.MembersListItemsAdapter
import com.projects.projemanag.databinding.DialogListBinding
import com.projects.projemanag.models.User


// TODO (Step 4: Create a members list dialog class to show the list of members in a dialog.)
// START
abstract class MembersListDialog(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = ""
) : Dialog(context) {

    private var adapter: MembersListItemsAdapter? = null
    private lateinit var binding : DialogListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())
        binding = DialogListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //setContentView(view)
        setContentView(binding?.root)
        //Esto para que cuando toquemos fuera del cuadro de dialogo se cierre
        setCanceledOnTouchOutside(true)
        //Para que puedar ser cancelable
        setCancelable(true)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.tvTitle.text = title
        if(list.size > 0){

            binding.rvList.layoutManager = LinearLayoutManager(context)
            adapter = MembersListItemsAdapter(context, list)
            binding.rvList.adapter = adapter

            adapter!!.setOnClickListener( object :
                MembersListItemsAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action: String) {
                    dismiss()
                    onItemSelected(user,action)
                }
            })

        }
    }

    protected abstract fun onItemSelected(user: User,action: String)
}
// END