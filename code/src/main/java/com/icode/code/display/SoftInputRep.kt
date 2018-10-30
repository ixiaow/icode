package com.icode.code.display

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * 编写人： xw
 * 创建时间：2018/10/12 15:08
 * 功能描述：输入法相关工具类
 */
object SoftInputRep {
    /**
     * 显示软键盘和隐藏软键盘
     *
     * @param view editText
     * @param show true 显示 ，false  隐藏软键盘
     */
    fun softInput(view: EditText?, show: Boolean) {

        val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let {
            when (show) {
                false -> it.hideSoftInputFromWindow(view.windowToken, 0)
                else -> {
                    view.requestFocus()
                    view.postDelayed({ it.showSoftInput(view, 0) }, 100)
                }
            }
        }
    }

}
