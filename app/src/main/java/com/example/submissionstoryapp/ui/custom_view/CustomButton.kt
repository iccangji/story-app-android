package com.example.submissionstoryapp.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.submissionstoryapp.R

class CustomButton: AppCompatButton{
    private lateinit var disableBackground: Drawable
    private lateinit var enableBackground: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = if(isEnabled) enableBackground else disableBackground
    }

    private fun init(){
        enableBackground = ContextCompat.getDrawable(context, R.drawable.bg_btn) as Drawable
        disableBackground = ContextCompat.getDrawable(context, R.drawable.bg_btn_disable) as Drawable
    }
}