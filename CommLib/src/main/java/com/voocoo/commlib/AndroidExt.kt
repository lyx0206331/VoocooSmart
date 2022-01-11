package com.voocoo.commlib

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.util.*

//                       _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                       O\ = /O
//                   ____/`---'\____
//                 .   ' \\| |// `.
//                  / \\||| : |||// \
//                / _||||| -:- |||||- \
//                  | | \\\ - /// | |
//                | \_| ''\---/'' | |
//                 \ .-\__ `-` ___/-. /
//              ______`. .' /--.--\ `. . __
//           ."" '< `.___\_<|>_/___.' >'"".
//          | | : `- \`.;`\ _ /`;.`/ - ` : | |
//            \ \ `-. \_ __\ /__ _/ .-` / /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//
//    .............................................
//             佛祖保佑             永无BUG
/**
 * author:RanQing
 * date:2021/7/17 0017 18:15
 * description:
 */

/**
 * 获取当前手机系统语言.例如：当前设置的是“中文-中国”，则返回“zh-CN”
 */
fun getSystemLanguage(): String = Locale.getDefault().language

/**
 * 获取当前语言列表
 */
fun getSystemLanguageList(): Array<Locale> = Locale.getAvailableLocales()

/**
 * 获取系统版本呈
 */
fun getSystemVersion(): String = android.os.Build.VERSION.RELEASE

/**
 * 获取手机型号
 */
fun getSystemModel(): String? = android.os.Build.MODEL

/**
 * 获取手机厂商
 */
fun getDeviceBrand(): String? = android.os.Build.BRAND

/**
 * 打开系统设置
 *
 */
internal fun Context.openSysSettings() = startActivity(Intent(Settings.ACTION_SETTINGS))

fun Context.showShortToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context.showLongToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun Context.showShortToast(@StringRes resId: Int) =
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()

fun Context.showLongToast(@StringRes resId: Int) =
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()

/**
 * 获取版本码
 */
fun Context.getVersionCode(): Int = packageManager.getPackageInfo(packageName, 0).versionCode

/**
 * 获取版本号
 */
fun Context.getVersionName(): String = packageManager.getPackageInfo(packageName, 0).versionName

fun Context.getColor(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

fun Activity.changeEntry(entryActNames: Array<String>, entryIndex: Int = 0) {
    val pm = this.packageManager
    entryActNames.forEachIndexed { index, name ->
        if (index != entryIndex) {
            pm.setComponentEnabledSetting(
                ComponentName(this, "${packageName}.$name"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        } else {
            pm.setComponentEnabledSetting(ComponentName(this, "${packageName}.$name"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        }
    }
}

fun Context.dp2px(dp: Int): Float = resources.displayMetrics.density * dp

fun Context.getThemePrimaryColor(): Int = TypedValue().also {
    this.theme.resolveAttribute(android.R.attr.colorPrimary, it, true)
}.data
