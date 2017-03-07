package com.javayhu.plugin;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private IPluginAidlInterface mPluginService;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textview);
        mTextView.setText(String.valueOf(Process.myPid()));
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPluginService = IPluginAidlInterface.Stub.asInterface(service);
            Log.d("plugin", String.valueOf(Process.myPid()));
            try {
                String result = mPluginService.getPlugin("service");
                Log.d("plugin", result);
                mTextView.setText(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPluginService = null;
        }
    };

    public void bindService(View view) {
        Intent intent = new Intent();
        intent.setClass(this, PluginService.class);
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }

    public void hookClipboard(View view) {
        try {
            ClipboardHelper.binder();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager.hasPrimaryClip()) {
            mTextView.setText(clipboardManager.getPrimaryClip().getItemAt(0).getText());
        }
    }
}
