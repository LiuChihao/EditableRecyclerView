package com.haivo.editablerecyclerview.widget.adapter

import android.content.Context
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haivo.editablerecyclerview.databinding.ItemEditableBinding
import com.haivo.editablerecyclerview.widget.AppSettingActivity
import com.haivo.editablerecyclerview.widget.bean.AppBean

class EditableRvTypeAdapter(context: Context, appSettingActivity: AppSettingActivity) :
    RecyclerView.Adapter<EditableRvTypeAdapter.ViewHolder>() {
    private var viewModel = appSettingActivity.viewModel
    private val acContext = context
    private var typeList = viewModel.typeList

    class ViewHolder(val bind: ItemEditableBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEditableBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(
            "TAG",
            "onBindViewHolder: $position+++++++++++++${viewModel.allAppList.filter { f -> f.type == position } as MutableList<EditableRvItemAdapter>}")
        holder.bind.itemEditableTypeName.text = typeList[position].typeName
        //---------------所有应用列表
//        holder.bind.editableRvItemRv.apply {
//            layoutManager =
//                GridLayoutManager(acContext,viewModel.spanCount)
//            adapter = adapterList[position]
//        }
//        val layoutManager = GridLayoutManager(acContext,5)
//        layoutManager.isAutoMeasureEnabled
//        holder.bind.editableRvItemRv.layoutManager = layoutManager
//        holder.bind.editableRvItemRv.adapter = viewModel.adapterList[position]
//        Log.d("TAG", "onBindViewHolder: $position+++++++++++++adapter set+++++++++++${viewModel.adapterList[position].allAppList.toString()}")
        val editableRvItemAdapter =
            EditableRvItemAdapter(viewModel.allAppList.filter { f -> f.type == position } as MutableList<AppBean>)
        val gridLayoutManager = GridLayoutManager(holder.itemView.context,5)
        holder.bind.editableRvItemRv.layoutManager = gridLayoutManager
        viewModel.adapterList.add(position,editableRvItemAdapter)
        holder.bind.editableRvItemRv.adapter = editableRvItemAdapter
        holder.bind.shapeLayout.setNormalRadius(100)
    }

    override fun getItemCount() = typeList.size
}