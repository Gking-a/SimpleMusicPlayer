package com.gking.simplemusicplayer.activity

import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack

class MyFunction {
    fun moreOnClick(){
        BottomMenu.show(arrayOf("播放", "收藏", "添加...", "还有啥啊？"))
            .setOnIconChangeCallBack(object : OnIconChangeCallBack(true) {
                override fun getIcon(bottomMenu: BottomMenu, index: Int, menuText: String): Int {
                    when (menuText) {
                        "播放" -> return android.R.mipmap.sym_def_app_icon
                        "收藏" -> return android.R.mipmap.sym_def_app_icon
                        "添加..." -> return android.R.mipmap.sym_def_app_icon
                        "还有啥啊？" -> return android.R.mipmap.sym_def_app_icon
                    }
                    return 0
                }
            })
            .setOnMenuItemClickListener { dialog, text, index ->
                print("我觉得这里不应该我来写")
                false
            }
    }
}