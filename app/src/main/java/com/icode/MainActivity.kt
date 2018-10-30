package com.icode

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.icode.code.link.LinkToApp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBtnLinkTaoBao.setOnClickListener {
            when (LinkToApp.isInstallApp(this, LinkToApp.TAOBAO_PACKAGE_NAME)) {
                false -> Toast.makeText(this,"手机上没有安装淘宝，无法唤醒", Toast.LENGTH_LONG).show()
                else -> LinkToApp.linkToApp(this,
                        "taobao://shop.m.taobao.com/shop/shop_index.htm?shop_id=131259851&spm=a230r.7195193." +
                                "1997079397.8.Pp3ZMM&point")
            }
        }
    }
}
