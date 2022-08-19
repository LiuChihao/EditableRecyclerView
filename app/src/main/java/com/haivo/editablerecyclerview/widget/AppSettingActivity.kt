package com.haivo.editablerecyclerview.widget

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haivo.editablerecyclerview.R
import com.haivo.editablerecyclerview.databinding.EditableRecyclerViewBinding
import com.haivo.editablerecyclerview.widget.adapter.CommonAppsAdapter
import com.haivo.editablerecyclerview.widget.adapter.EditableRvItemAdapter
import com.haivo.editablerecyclerview.widget.adapter.EditableRvTypeAdapter
import com.haivo.editablerecyclerview.widget.bean.AppBean
import com.haivo.editablerecyclerview.widget.bean.TypeBean
import java.util.*

class AppSettingActivity : AppCompatActivity() {

    private lateinit var binding: EditableRecyclerViewBinding
    private lateinit var commonAppsAdapter: CommonAppsAdapter
    private lateinit var editableTypeAdapter: EditableRvTypeAdapter
    val viewModel by lazy { ViewModelProvider(this).get(EditableRvViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditableRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //--------------------------首页应用
        commonAppsAdapter = CommonAppsAdapter(this)
        binding.commonAppName.text = viewModel.commonName
        binding.shapeLayout.setNormalRadius(R.drawable.bg_radius_8dp_ff)
        binding.commonAppRv.apply {
            layoutManager =
                GridLayoutManager(this@AppSettingActivity, 5, LinearLayoutManager.VERTICAL, false)
            adapter = commonAppsAdapter
            Log.d("TAG", "onCreate: ${viewModel.homeAppList.size}")
            Log.d("TAG", "onCreate: $commonAppsAdapter")
        }
        //-------------------------应用分类
        editableTypeAdapter = EditableRvTypeAdapter(this@AppSettingActivity,this)
        binding.allAppRv.apply {
            layoutManager =
                LinearLayoutManager(this@AppSettingActivity)
            adapter = editableTypeAdapter
        }
        setClick(this)
    }

    fun setClick(context: Context) {
        binding.editRvButton.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                //修改编辑文字
                binding.editRvButton.text = "完成"
                binding.editRvButton.setTextColor(Color.parseColor("#008EFF"))
                //设置可编辑
                commonAppsAdapter.isInEditing = true
                //设置首页应用可删除
                commonAppsAdapter.data.onEach { it ->
                    it.option = AppBean.Companion.Option.REMOVE
                }
                commonAppsAdapter.notifyDataSetChanged()
                enableDragItem(true)
                //设置各分类列表
                for (i in 0 until viewModel.adapterList.size) {
                    viewModel.adapterList[i].isInEditing = true
                    viewModel.adapterList[i].allAppList.onEach { allAppItem ->
                        // 遍历列表1中元素, 如果与列表2图标相同, 则不显示+号
                        val isExistSameElement =
                            commonAppsAdapter.data.any { commonAppItem ->
                                commonAppItem.name == allAppItem.name
                            }
                        allAppItem.option =
                            if (isExistSameElement) {
                                AppBean.Companion.Option.NONE
                            } else {
                                AppBean.Companion.Option.ADD
                            }
                        viewModel.adapterList[i].notifyDataSetChanged()
                    }
                }
            } else {
                //编辑
                binding.editRvButton.text = "编辑"
                binding.editRvButton.setTextColor(Color.parseColor("#A0A0A0"))
                //设置不可编辑
                commonAppsAdapter.isInEditing = false
                commonAppsAdapter.data.onEach { commonItem ->
                    commonItem.option = AppBean.Companion.Option.NONE
                }
                commonAppsAdapter.notifyDataSetChanged()

                enableDragItem(false)
                for (i in 0 until viewModel.adapterList.size) {
                    viewModel.adapterList[i].isInEditing = false
                    viewModel.adapterList[i].allAppList.onEach { allAppItem ->
                        allAppItem.option = AppBean.Companion.Option.NONE
                    }
                    viewModel.adapterList[i].notifyDataSetChanged()
                    Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
                }
                for (i in 0 until viewModel.homeAppList.size){
                    Log.d("setting success", "setClick: ${viewModel.homeAppList[i].name} ")
                }
            }
            //删除后
            commonAppsAdapter.onRemoveBtnClickListener =
                object : CommonAppsAdapter.OnRemoveBtnClickListener {
                    override fun onClick(view: View, appBean: AppBean) {
                        //找到列表2中与列表1被删除的图标相同的那个元素, 并将其重新变为可添加状态
                        var theSameElementIndex = -1
                        var forI = -1
                        for (i in 0 until viewModel.adapterList.size){
                            var int = viewModel.adapterList[i].allAppList.indexOfFirst { it.name == appBean.name }
                            Log.d("TAGlog", "onClick:${viewModel.adapterList.size}------${viewModel.adapterList[i].allAppList.size} ")
                            if (int != -1){
                                forI = i
                                theSameElementIndex = int
                            }
                        }
//                        val theSameElementIndex =
//                            viewModel.allAppList.indexOfFirst { it.uid == appBean.uid }
                        if (theSameElementIndex < 0) return
                        viewModel.adapterList[forI].allAppList[theSameElementIndex].option =
                            AppBean.Companion.Option.ADD
//                        for (i in 0 until viewModel.adapterList.size) {
//                            viewModel.adapterList[i].notifyDataSetChanged()
//                        }
                        viewModel.adapterList[forI].notifyDataSetChanged()
                    }
                }
            //添加首页应用
            for (i in 0 until viewModel.adapterList.size) {
                viewModel.adapterList[i].onAddBtnClickListener =
                    object : EditableRvItemAdapter.OnAddBtnClickListener {
                        override fun onClick(view: View, appBean: AppBean) {
                            if (commonAppsAdapter.data.size >= commonAppsAdapter.maxCount) {
                                Toast.makeText(context, "超出最大个数限制,无法添加", Toast.LENGTH_SHORT).show()
                                return
                            }
                            val newAppBean = AppBean().apply {
                                uid = appBean.uid
                                name = appBean.name
                                option = AppBean.Companion.Option.REMOVE
                                imagePath = appBean.imagePath
                            }
                            appBean.option = AppBean.Companion.Option.NONE
                            commonAppsAdapter.data.add(newAppBean)
                            commonAppsAdapter.notifyItemInserted(commonAppsAdapter.data.size)
                            viewModel.adapterList[i].notifyDataSetChanged()
                        }
                    }
            }
        }
    }

    //设置ItemTouchHelper
    private fun enableDragItem(enable: Boolean) {
        if (enable) {
            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    return makeMovementFlags(
                        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
                        0
                    )
                }

                //松开
                override fun onMove(
                    recyclerView: RecyclerView,
                    oldHolder: RecyclerView.ViewHolder,
                    targerHolder: RecyclerView.ViewHolder
                ): Boolean {
                    commonAppsAdapter.notifyItemMoved(
                        oldHolder.adapterPosition,
                        targerHolder.adapterPosition
                    )
                    // 在每次移动后, 将界面上图标的顺序同步到appsAdapter.data中
                    val newData = mutableListOf<Pair<String, Int>>()
                    commonAppsAdapter.data.forEachIndexed { index, appBean ->
                        val holder =
                            recyclerView.findViewHolderForAdapterPosition(index) as CommonAppsAdapter.ViewHolder
                        newData.add(Pair(holder.funcUrl, index))
                    }
                    for (i in newData) {
                        val sameFuncIndex =
                            commonAppsAdapter.data.indexOfFirst { i.first == it.uid }
                        Collections.swap(commonAppsAdapter.data, i.second, sameFuncIndex)
                    }
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

                override fun canDropOver(
                    recyclerView: RecyclerView,
                    current: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = true

                override fun isLongPressDragEnabled() = false
            })

            commonAppsAdapter.dragOverListener = object : CommonAppsAdapter.DragOverListener {
                override fun startDragItem(holder: RecyclerView.ViewHolder) {
                    itemTouchHelper.startDrag(holder)
                }
            }
            itemTouchHelper.attachToRecyclerView(binding.commonAppRv)
        } else {
            commonAppsAdapter.dragOverListener = null
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.homeAppList.addAll(mutableListOf(
            com.haivo.editablerecyclerview.widget.bean.AppBean().apply {
                uid = "uid_001"
                name = "网上药房"
                imagePath = R.drawable.ic_home_rv_item_wsyf
                type = 0
                action = 1
            }, AppBean().apply {
                uid = "uid_010"
                name = "视频医生"
                imagePath = R.drawable.ic_home_rv_item_spys
                type = 3
                action = 1
            }, AppBean().apply {
                uid = "uid_007"
                name = "门诊绿通"
                imagePath = R.drawable.ic_home_rv_item_mzlt
                type = 2
                action = 1
            }, AppBean().apply {
                uid = "uid_004"
                name = "体检"
                imagePath = R.drawable.ic_home_rv_item_tj
                type = 1
                action = 1
            }, AppBean().apply {
                uid = "uid_006"
                name = "齿科"
                imagePath = R.drawable.ic_home_rv_item_ck
                type = 1
                action = 1
            }, AppBean().apply {
                uid = "uid_002"
                name = "送药到家"
                imagePath = R.drawable.ic_home_rv_item_sydj
                type = 0
                action = 1
            }, AppBean().apply {
                uid = "uid_005"
                name = "中医"
                imagePath = R.drawable.ic_home_rv_item_zy
                type = 1
                action = 1
            }, AppBean().apply {
                uid = "uid_013"
                name = "急救手册"
                imagePath = R.drawable.ic_home_rv_item_jjsc
                type = 3
                action = 1
            }, AppBean().apply {
                uid = "uid_008"
                name = "精准影像"
                imagePath = R.drawable.ic_home_rv_item_jzyx
                type = 2
                action = 1
            }
        ))

        viewModel.typeList.addAll(mutableListOf(
            TypeBean().apply {
                typeId = 0
                typeName = "健康服务"
            }, TypeBean().apply {
                typeId = 1
                typeName = "健康预约"
            }, TypeBean().apply {
                typeId = 2
                typeName = "健康医疗"
            }, TypeBean().apply {
                typeId = 3
                typeName = "健康咨询"
            }, TypeBean().apply {
                typeId = 4
                typeName = "个人中心"
            }
        ))

//        viewModel.adapterList.addAll(getTypeAdapter(viewModel.typeList))

        viewModel.allAppList.addAll(mutableListOf(
            AppBean().apply {
                uid = "udi_001"
                name = "网上药房"
                imagePath = R.drawable.ic_home_rv_item_wsyf
                type = 0
                action = 1
            }, AppBean().apply {
                uid = "udi_002"
                name = "送药到家"
                imagePath = R.drawable.ic_home_rv_item_sydj
                type = 0
                action = 1
            }, AppBean().apply {
                uid = "udi_003"
                name = "特惠商城"
                imagePath = R.drawable.ic_icon_all_app_thsc
                type = 0
                action = 1
            }, AppBean().apply {
                uid = "udi_004"
                name = "体检"
                imagePath = R.drawable.ic_home_rv_item_tj
                type = 1
                action = 1
            }, AppBean().apply {
                uid = "udi_005"
                name = "中医"
                imagePath = R.drawable.ic_home_rv_item_zy
                type = 1
                action = 1
            }, AppBean().apply {
                uid = "udi_006"
                name = "齿科"
                imagePath = R.drawable.ic_home_rv_item_ck
                type = 1
                action = 1
            }, AppBean().apply {
                uid = "udi_007"
                name = "门诊绿通"
                imagePath = R.drawable.ic_home_rv_item_mzlt
                type = 2
                action = 1
            }, AppBean().apply {
                uid = "udi_008"
                name = "精准影像"
                imagePath = R.drawable.ic_home_rv_item_jzyx
                type = 2
                action = 1
            }, AppBean().apply {
                uid = "udi_009"
                name = "海外医疗"
                imagePath = R.drawable.ic_icon_all_app_hwyl
                type = 2
                action = 1
            }, AppBean().apply {
                uid = "udi_010"
                name = "视频医生"
                imagePath = R.drawable.ic_home_rv_item_spys
                type = 3
                action = 1
            }, AppBean().apply {
                uid = "udi_011"
                name = "疾病自查"
                imagePath = R.drawable.ic_icon_all_app_jbzc
                type = 3
                action = 1
            }, AppBean().apply {
                uid = "udi_012"
                name = "用药助手"
                imagePath = R.drawable.ic_icon_all_app_yyzs
                type = 3
                action = 1
            }, AppBean().apply {
                uid = "udi_013"
                name = "急救手册"
                imagePath = R.drawable.ic_home_rv_item_jjsc
                type = 3
                action = 1
            }, AppBean().apply {
                uid = "udi_014"
                name = "健康资讯"
                imagePath = R.drawable.ic_icon_all_app_jkzx
                type = 3
                action = 1
            }, AppBean().apply {
                uid = "udi_015"
                name = "我的订单"
                imagePath = R.drawable.ic_icon_all_app_wddd
                type = 4
                action = 1
            }, AppBean().apply {
                uid = "udi_016"
                name = "设置"
                imagePath = R.drawable.ic_icon_all_app_setting
                type = 4
                action = 1
            }, AppBean().apply {
                uid = "udi_017"
                name = "联系客服"
                imagePath = R.drawable.ic_icon_all_app_lxkf
                type = 4
                action = 1
            }, AppBean().apply {
                uid = "udi_018"
                name = "版本信息"
                imagePath = R.drawable.ic_icon_all_app_bbxx
                type = 4
                action = 1
            }
        ))
    }

//    private fun getTypeAdapter(typeList: MutableList<TypeBean>): MutableList<EditableRvItemAdapter> {
//        var adapterList: MutableList<EditableRvItemAdapter> = mutableListOf()
//        for (i in 0 until typeList.size) {
//            adapterList.add(EditableRvItemAdapter(viewModel.allAppList.filter { f -> f.type == i } as MutableList<AppBean>))
//        }
//        return adapterList
//    }
}