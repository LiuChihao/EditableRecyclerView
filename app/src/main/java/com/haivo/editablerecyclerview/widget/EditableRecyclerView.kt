package com.haivo.editablerecyclerview.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import com.haivo.editablerecyclerview.R
import com.tre.adev.view.RecyclerView
import com.tre.adev.view.extensions.getThemeColorPrimary

class EditableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0
): ConstraintLayout(context, attrs, defStyle){
    private var commonAppName: TextView
    private var commonAppRv: RecyclerView

    private var mView =
        LayoutInflater.from(context).inflate(R.layout.editable_recycler_view, this, true)

    init {
        commonAppName = mView.findViewById(R.id.common_app_name)
        commonAppRv = mView.findViewById(R.id.common_app_rv)

        context.theme.obtainStyledAttributes(attrs, R.styleable.ActionBar, defStyle, 0).apply {
            try {

            } finally {
                recycle()
            }
        }
    }
}