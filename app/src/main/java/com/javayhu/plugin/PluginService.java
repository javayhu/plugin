package com.javayhu.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;

public class PluginService extends Service {

    public PluginService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private IPluginAidlInterface.Stub binder = new IPluginAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String getPlugin(String name) throws RemoteException {
            return name + " " + Process.myPid();
        }
    };

}
