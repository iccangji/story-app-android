package com.example.submissionstoryapp.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.example.submissionstoryapp.R

class PasswordEditText: AppCompatEditText, View.OnTouchListener {
    private lateinit var seekText: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        seekText = ContextCompat.getDrawable(context, R.drawable.baseline_remove_red_eye_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("length", p0?.length.toString())
                if (p0.toString().isNotEmpty()) showSeekButton() else hideSeekButton()
                if(p0?.toString()?.length!! > 7){
                    background = ContextCompat.getDrawable(context,
                        R.drawable.rounded_corner_edit_text)
                    error = null
                }
                else{
                    background = ContextCompat.getDrawable(context,
                        R.drawable.rounded_edit_corner_error)
                    error = resources.getString(R.string.password_not_valid)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }

        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setPadding(20)
    }

    private fun showSeekButton() {
        setButtonDrawables(endOfTheText = seekText)
    }

    private fun hideSeekButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        var editTextCursor: Int? = null
        if (compoundDrawables[2] != null) {
            val seekButtonStart: Float
            val seekButtonEnd: Float
            var isSeekButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                seekButtonEnd = (seekText.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < seekButtonEnd -> isSeekButtonClicked = true
                }
            } else {
                seekButtonStart = (width - paddingEnd - seekText.intrinsicWidth).toFloat()
                when {
                    event.x > seekButtonStart -> isSeekButtonClicked = true
                }
            }
            if (isSeekButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        seekText = ContextCompat.getDrawable(
                            context,
                            R.drawable.baseline_remove_red_eye_24
                        ) as Drawable
                        editTextCursor = this.selectionStart
                        this.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        setSelection(editTextCursor)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        seekText = ContextCompat.getDrawable(
                            context,
                            R.drawable.baseline_remove_red_eye_24
                        ) as Drawable
                        editTextCursor = this.selectionStart
                        this.transformationMethod = PasswordTransformationMethod.getInstance()
                        this.setSelection(editTextCursor)
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }
}