package com.javayhu.plugin;

import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * hook系统剪切板服务
 * http://blog.csdn.net/yulong0809/article/details/56842027
 * <p>
 * Created by 刘镓旗 on 2017/1/22.
 */
public class ClipboardHelper {

    public static void binder() {
        try {
            //1.剪切板服务，是在系统的ServiceManager中的getSerVice方法中得到的，我们先拿到ServiceManager
            Class<?> serviceMangerClass = Class.forName("android.os.ServiceManager");
            //2.拿到getService方法
            Method getServiceMethod = serviceMangerClass.getDeclaredMethod("getService", String.class);
            //3.通过这个方法，拿到原本的系统服务代理对象
            IBinder binder = (IBinder) getServiceMethod.invoke(null, "clipboard");
            //4.我们通过这个对象，创建我们自己的代理对象，瞒天过海骗过系统
            IBinder myBinder = (IBinder) Proxy.newProxyInstance(serviceMangerClass.getClassLoader(),
                    new Class[]{IBinder.class}
                    , new ClipboardProxy(binder)
            );
            //5.拿到ServiceManager中的数组
            Field field = serviceMangerClass.getDeclaredField("sCache");
            field.setAccessible(true);
            Map<String, IBinder> map = (Map) field.get(null);
            //将我们的服务类存入map
            map.put("clipboard", myBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}