package com.projects.projemanag.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.projemanag.adapters.LabelColorListItemsAdapter
import com.projects.projemanag.databinding.DialogListBinding


// TODO (Create an dialogs package and a class for showing the label color list dialog.)
abstract class LabelColorListDialog(
    context: Context,
    private var list : ArrayList<String>,
    private val title : String = "",
    private var mSelectedColor : String = ""
//Heredamos de la clase Dialog y le pasamos el contexto
) : Dialog(context) {

    private var adapter : LabelColorListItemsAdapter? = null
    private lateinit var binding: DialogListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun setUpRecyclerView(){
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        binding.rvList.adapter = adapter

        adapter!!.onClickListener = object : LabelColorListItemsAdapter.OnClickListener{
            override fun onClick(position: Int, color : String) {
                dismiss()
                onItemSelected(color)
            }

        }
    }

    //como nuestra clase es abstracta debemos crear funciones abstractas
    protected abstract fun onItemSelected(color: String)
}