package top.lbqaq.nosese;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("ceui.lisa.pixiv")){
            XposedHelpers.findAndHookMethod("ceui.lisa.activities.MainActivity", lpparam.classLoader, "initView", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Field f = param.thisObject.getClass().getDeclaredField("userHead");
                    f.setAccessible(true);
                    ImageView v = (ImageView) f.get(param.thisObject);
//                v.setLongClickable(false);
                    v.setOnLongClickListener(v1 -> {
                        Toast.makeText((Activity) param.thisObject, "不许涩涩！", Toast.LENGTH_SHORT).show();
                        return true;
                    });
                }
            });

//            XposedHelpers.findAndHookMethod("ceui.lisa.activities.MainActivity", lpparam.classLoader, "initFragment", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    Toast.makeText((Activity) param.thisObject, "不许涩涩！", Toast.LENGTH_SHORT).show();
//                    ((Activity) param.thisObject).finish();
//                }
//            });
        }


        if(lpparam.packageName.equals("com.xjs.ehviewer")){
            XposedHelpers.findAndHookMethod("com.hippo.ehviewer.ui.MainActivity", lpparam.classLoader, "initUserImage", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Toast.makeText((Activity) param.thisObject, "不许涩涩！", Toast.LENGTH_SHORT).show();
                    ((Activity) param.thisObject).finish();
                }
            });
        }

        if(lpparam.packageName.equals("com.picacomic.fregata")){
            XposedHelpers.findAndHookMethod("com.picacomic.fregata.activities.SplashActivity", lpparam.classLoader, "onCreate", Bundle.class ,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Toast.makeText((Activity) param.thisObject, "不许涩涩！", Toast.LENGTH_SHORT).show();
                    ((Activity) param.thisObject).finish();
                }
            });
        }

//        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                Class clazz = (Class) param.getResult();
//                String clazzName = clazz.getName();
//                //排除非包名的类
//                if(clazzName.contains("ceui.lisa")){
//                    Method[] mds = clazz.getDeclaredMethods();
//                    for(int i =0;i<mds.length;i++){
//                        final Method md = mds[i];
//                        int mod = mds[i].getModifiers();
//                        //去除抽象、native、接口方法
//                        if(!Modifier.isAbstract(mod)
//                                && !Modifier.isNative(mod)
//                                &&!Modifier.isAbstract(mod)){
//                            XposedBridge.hookMethod(mds[i], new XC_MethodHook() {
//                                @Override
//                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                    super.beforeHookedMethod(param);
//                                    Log.d("lbqaq",md.toString());
//                                }
//                            });
//                        }
//
//                    }
//                }
//
//            }
//        });


    }

}
