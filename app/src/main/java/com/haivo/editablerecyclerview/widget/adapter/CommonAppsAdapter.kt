package com.haivo.editablerecyclerview.widget.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haivo.editablerecyclerview.AppsHolder
import com.haivo.editablerecyclerview.R
import com.haivo.editablerecyclerview.databinding.ItemEditableItemAppBinding
import com.haivo.editablerecyclerview.widget.AppSettingActivity
import com.haivo.editablerecyclerview.widget.EditableRvViewModel
import com.haivo.editablerecyclerview.widget.bean.AppBean

class CommonAppsAdapter(appSettingActivity: AppSettingActivity) : RecyclerView.Adapter<CommonAppsAdapter.ViewHolder>() {
//    private val homeAppList: MutableList<AppBean> = EditableRvViewModel().homeAppList
    private val activity = appSettingActivity
    //最大item个数
    var maxCount = EditableRvViewModel().homeMaxCount
    var onRemoveBtnClickListener: OnRemoveBtnClickListener? = null
    var isInEditing = false //是否处于编辑状态，编辑状态时点击不能跳转
    var data = activity.viewModel.homeAppList
     var dragOverListener: DragOverListener?= null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_editable_item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("TAG", "onBindViewHolder: ************************ ${data.size}")
        val appBean = data[holder.adapterPosition]
        //图标设置
        Glide.with(holder.itemView.context).load(appBean.imagePath).into(holder.bind.itemEditableItemAppImage)
        //状态图标设置
        holder.bind.itemEditableItemAppOption.setImageResource(when (appBean.option){
            AppBean.Companion.Option.ADD -> {
                holder.bind.itemEditableItemAppOption.visibility = View.VISIBLE
                R.drawable.ic_editable_rv_add
            }
            AppBean.Companion.Option.REMOVE -> {
                holder.bind.itemEditableItemAppOption.visibility = View.VISIBLE
                holder.bind.itemEditableItemAppOption.setOnClickListener {
                    onRemoveBtnClickListener?.onClick(it,appBean)
                    data.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }
                R.drawable.ic_editable_rv_remove
            }
            AppBean.Companion.Option.NONE ->{
                holder.bind.itemEditableItemAppOption.visibility = View.GONE
                R.drawable.null_palceholder
            }
        })
        //APP name 设置
        holder.bind.itemEditableItemAppName.text = appBean.name
        holder.funcUrl = appBean.uid
        //App 跳转设置
        holder.bind.root.setOnClickListener {
            if (appBean.action!=null && !isInEditing)
            Toast.makeText(holder.itemView.context,"跳转",Toast.LENGTH_SHORT).show()
        }
        //拖动监听
        if (dragOverListener != null){
            holder.bind.root.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN){
                    dragOverListener?.startDragItem(holder)
                }
                return@setOnTouchListener false
            }
        }
    }

    override fun getItemCount(): Int {
        return if (data.size > maxCount) maxCount else data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bind = ItemEditableItemAppBinding.bind(itemView)
        var funcUrl = ""
    }
    interface OnRemoveBtnClickListener{
        fun onClick(view: View,appBean: AppBean)
    }

    interface DragOverListener{
        fun startDragItem(holder: RecyclerView.ViewHolder)
    }
}