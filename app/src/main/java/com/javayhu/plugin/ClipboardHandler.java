package com.javayhu.plugin;

import android.content.ClipData;
import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * hook系统剪切板服务
 * http://blog.csdn.net/yulong0809/article/details/56842027
 * <p>
 * Created by 刘镓旗 on 2017/1/22.
 */
public class ClipboardHandler implements InvocationHandler {

    private Object mBase;

    public ClipboardHandler(IBinder base, Class stub) {
        //拿到asInteface方法，因为源码中执行了这一句，我们也要执行这一句
        try {
            Method asInterface = stub.getDeclaredMethod("asInterface", IBinder.class);
            mBase = asInterface.invoke(null, base);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //这里我们拦截粘贴的方法
        if ("getPrimaryClip".equals(method.getName())) {
            return ClipData.newPlainText(null, "Clipboard is hooked!");
        }
        //再拦截是否有复制的方法，放系统认为一直都有
        if ("hasPrimaryClip".equals(method.getName())) {
            return true;
        }
        //其他情况还是调用原来的方法并返回原来的结果
        return method.invoke(mBase, args);
    }
}