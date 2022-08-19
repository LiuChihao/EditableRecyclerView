package com.haivo.editablerecyclerview.widget

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haivo.editablerecyclerview.R
import com.haivo.editablerecyclerview.widget.adapter.EditableRvItemAdapter
import com.haivo.editablerecyclerview.widget.bean.AppBean
import com.haivo.editablerecyclerview.widget.bean.TypeBean

class EditableRvViewModel : ViewModel() {
    var commonName : String = "首页应用"
    var spanCount : Int = 5
    var homeAppList: MutableList<AppBean> = mutableListOf()

    var allAppList: MutableList<AppBean> = mutableListOf()

    var homeMaxCount : Int = 9

    var typeList: MutableList<TypeBean> = mutableListOf()

    var adapterList : MutableList<EditableRvItemAdapter> = mutableListOf()

}