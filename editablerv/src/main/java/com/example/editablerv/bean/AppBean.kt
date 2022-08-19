package com.example.editablerv.bean

import android.os.Parcel
import android.os.Parcelable

class AppBean() : Parcelable{
    //id
    var uid: String = ""
    //appName
    var name: String = ""
    //image path
    var imagePath: Int = -1
    //type
    var type: Int = 1
    //显示状态
    var option: Option = Option.NONE
    //图标跳转
    var action: Int = 1

    companion object{
        enum class Option{
            ADD, REMOVE, NONE
        }
    }

    constructor(parcel: Parcel) : this() {
        name = parcel.readString() ?: ""
        uid = parcel.readString() ?: ""
        imagePath = parcel.readInt()
        type = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(uid)
        parcel.writeInt(imagePath)
        parcel.writeInt(type)
//        parcel.writeParcelable(action,flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    @JvmField
    val CREATOR = object  : Parcelable.Creator<AppBean> {
        override fun createFromParcel(parcel: Parcel): AppBean {
            return AppBean(parcel)
        }

        override fun newArray(size: Int): Array<AppBean?> {
            return arrayOfNulls(size)
        }
    }
}