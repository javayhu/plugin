package com.javayhu.plugin;

import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * hook系统剪切板服务
 * http://blog.csdn.net/yulong0809/article/details/56842027
 * <p>
 * Created by 刘镓旗 on 2017/1/22.
 */
public class ClipboardProxy implements InvocationHandler {

    private final IBinder mBase;

    //这里传入的是原系统的代理类
    public ClipboardProxy(IBinder binder) {
        mBase = binder;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //拦截原系统类查询本地是否有这个代理的方法
        if ("queryLocalInterface".equals(method.getName())) {
            //我们这里要创建我们自己的系统类，然后返回
            //1.拿到系统的aidl类中的stub，因为这个对象本来就是个代理,而且源码执行了
            /*static private IClipboard getService() {
                synchronized (sStaticLock) {
                    if (sService != null) {
                        return sService;
                    }
                    IBinder b = ServiceManager.getService("clipboard");
                    sService = IClipboard.Stub.asInterface(b);
                    return sService;
                }
            }*/
            Class<?> mStubClass = Class.forName("android.content.IClipboard$Stub");
            //2.再拿到IClipboard本地对象类
            Class<?> mIClipboard = Class.forName("android.content.IClipboard");
            //3.创建我们自己的代理
            return Proxy.newProxyInstance(mStubClass.getClassLoader(),
                    new Class[]{mIClipboard},
                    new ClipboardHandler(mBase, mStubClass));
        }
        //不是这个方法还是调用原系统的来执行
        return method.invoke(mBase, args);
    }
}