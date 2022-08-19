package com.haivo.editablerecyclerview.widget.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haivo.editablerecyclerview.R
import com.haivo.editablerecyclerview.databinding.ItemEditableItemAppBinding
import com.haivo.editablerecyclerview.widget.bean.AppBean

class EditableRvItemAdapter(
    mutableList: MutableList<AppBean>
) : RecyclerView.Adapter<EditableRvItemAdapter.ViewHolder>() {
    var allAppList = mutableList
    var isInEditing = false //是否处于编辑状态, 编辑状态时点击不能跳转
    var onAddBtnClickListener: OnAddBtnClickListener? = null
    class ViewHolder(val bind: ItemEditableItemAppBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemEditableItemAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("TAGonBindViewHolder", "onBindViewHolder: $allAppList")
        //get app bean
        val appBean = allAppList[position]
        //set image
        Glide.with(holder.itemView.context).load(appBean.imagePath)
            .into(holder.bind.itemEditableItemAppImage)
        //set Option
        holder.bind.itemEditableItemAppOption.setImageResource(
            when (appBean.option) {
                AppBean.Companion.Option.ADD -> {
                    holder.bind.itemEditableItemAppOption.visibility = View.VISIBLE
                    holder.bind.itemEditableItemAppOption.setOnClickListener {
                        onAddBtnClickListener?.onClick(it,appBean)
                    }
                    R.drawable.ic_editable_rv_add
                }
                AppBean.Companion.Option.REMOVE -> {
                    holder.bind.itemEditableItemAppOption.visibility = View.VISIBLE
                    holder.bind.itemEditableItemAppOption.setOnClickListener {
                        allAppList.removeAt(holder.bindingAdapterPosition)
                        notifyItemRemoved(holder.bindingAdapterPosition)
                    }
                    R.drawable.ic_editable_rv_remove
                }
                AppBean.Companion.Option.NONE -> {
                    holder.bind.itemEditableItemAppOption.visibility  = View.INVISIBLE
                    R.drawable.null_palceholder
                }
            })
        //icon
        Glide.with(holder.itemView.context).load(appBean.imagePath).into(holder.bind.itemEditableItemAppImage)
        //name
        holder.bind.itemEditableItemAppName.text = appBean.name
        //action
        holder.bind.root.setOnClickListener {
            if (appBean.action != null && !isInEditing){
                Toast.makeText(holder.itemView.context,"跳转",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = allAppList.size

    interface OnAddBtnClickListener {
        fun onClick(view: View, appBean: AppBean)
    }
}