package top.lbqaq.nosese

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val xsp = XSharedPreferences("top.lbqaq.nosese","config")

        if (lpparam.packageName == "ceui.lisa.pixiv"){
            XposedHelpers.findAndHookMethod("ceui.lisa.activities.MainActivity", lpparam.classLoader, "initView",object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam){
                    if (isTimeOk(xsp)) return
                    val f = param.thisObject.javaClass.getDeclaredField("userHead")
                    f.isAccessible = true
                    val v = f[param.thisObject] as ImageView
                    v.setOnLongClickListener{
//                        Toast.makeText(param.thisObject as Activity,settingDateText,Toast.LENGTH_SHORT).show()
                        Toast.makeText(param.thisObject as Activity,"不许涩涩！",Toast.LENGTH_SHORT).show()
                        true
                    }
            }
            })
        }

        if (lpparam.packageName == "com.xjs.ehviewer"){
            XposedHelpers.findAndHookMethod("com.hippo.ehviewer.ui.MainActivity", lpparam.classLoader, "initUserImage",object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam){
                    if (isTimeOk(xsp)) return
                    Toast.makeText(param.thisObject as Activity,"不许涩涩！",Toast.LENGTH_SHORT).show()
                    (param.thisObject as Activity).finish()
                }
            })
        }

        if (lpparam.packageName == "com.picacomic.fregata"){
            XposedHelpers.findAndHookMethod("com.picacomic.fregata.activities.SplashActivity", lpparam.classLoader, "onCreate",Bundle::class.java,object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam){
                    if (isTimeOk(xsp)) return
                    Toast.makeText(param.thisObject as Activity,"不许涩涩！",Toast.LENGTH_SHORT).show()
                    (param.thisObject as Activity).finish()
                }
            })
        }
    }

    private fun isTimeOk(xsp: XSharedPreferences) : Boolean{
        val settingDateText = xsp.getString("settingDate","-1")
        if (settingDateText == "-1")
            return true
        val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val settingDate = LocalDateTime.parse(settingDateText,formatter)
        val localDateTime = LocalDateTime.now()
        return localDateTime.isAfter(settingDate)
    }

}