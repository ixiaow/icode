package com.icode.code.link

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils

/**
 * 编写人： xw
 * 创建时间：2018/10/30 16:31
 * 功能描述：唤醒其他app
 */

object LinkToApp {
    /**
     * 淘宝包名
     */
    const val TAOBAO_PACKAGE_NAME = "com.taobao.taobao"

    /**
     * 判断指定包名的app是否被安装
     * @context 上下文
     * @packageName 需要打开应用程序的包名
     */
    fun isInstallApp(context: Context, packageName: String): Boolean {

        if (TextUtils.isEmpty(packageName)) {
            return false
        }
        return try {
            val flag = if (Build.VERSION.SDK_INT >= 24) {
                PackageManager.MATCH_UNINSTALLED_PACKAGES
            } else {
                PackageManager.GET_UNINSTALLED_PACKAGES
            }
            context.packageManager.getApplicationInfo(packageName, flag)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * 连接到某个app,为了自定义处理不存在的情况，所以此方法只是单纯的进行跳转
     * @context shangxiawe
     * @url 需要跳转的url
     */
    fun linkToApp(context: Context, url: String) {
        val intent = Intent()
        val uri = Uri.parse(url)

        intent.action = Intent.ACTION_VIEW
        intent.data = uri
        context.startActivity(intent)
    }

}