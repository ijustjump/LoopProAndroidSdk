package com.example.loopprosdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.loopprosdk.R;
import com.loop.loopprosdk.LoopHost;
import com.loop.loopprosdk.LoopPro;
import com.loop.loopprosdk.LoopDevice;
import com.loop.loopprosdk.LoopMessage;

public class MainActivity extends AppCompatActivity {
    private LoopPro loopMax;
    private LoopDevice loopDevice;
    private LoopHost loopHost;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case LoopPro.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case LoopPro.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case LoopPro.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case LoopPro.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case LoopPro.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            loopMax = ((LoopPro.UsbBinder) arg1).getService();
            Log.d("LOOP", "LOOP");
            loopMax.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            loopMax = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MyHandler();
        Button button1 = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button button5 = findViewById(R.id.button5);
        Button button6 = findViewById(R.id.button6);
        Button button7 = findViewById(R.id.button7);
        Button button8 = findViewById(R.id.button8);
        Button button9 = findViewById(R.id.button9);
        Button button10 = findViewById(R.id.button10);
        Button button11 = findViewById(R.id.button11);
        Button button12 = findViewById(R.id.button12);
        button1.setOnClickListener(v -> loopMax.getHostInfo());
        button2.setOnClickListener(v -> loopMax.openPairMode());
        button3.setOnClickListener(v -> loopMax.closePairMode());
        button4.setOnClickListener(v -> loopMax.pairDevice(1));
        button5.setOnClickListener(v -> {
            if (loopDevice != null) {
                loopMax.connectDevice(loopDevice);
            }
        });
        button6.setOnClickListener(v -> {
            if (loopDevice != null) {
                loopMax.disconnectDevice(loopDevice);
            }
        });
        button7.setOnClickListener(v -> loopMax.getPower());
        button8.setOnClickListener(v -> loopMax.getCount());
        button9.setOnClickListener(v -> loopMax.startJumpLimitTime(180));
        button10.setOnClickListener(v -> loopMax.startJumpLimitCount(100));
        button11.setOnClickListener(v -> loopMax.stopJump());
        button12.setOnClickListener(v -> loopMax.powerOffDevice());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(usbConnection); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(ServiceConnection serviceConnection) {
        if (!LoopPro.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, LoopPro.class);
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, LoopPro.class);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoopPro.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(LoopPro.ACTION_NO_USB);
        filter.addAction(LoopPro.ACTION_USB_DISCONNECTED);
        filter.addAction(LoopPro.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(LoopPro.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LoopPro.LOOP_PAIR_DEVICE_INFO:
                    loopDevice = (LoopDevice) msg.obj;
                    Log.d("LoopDevice", loopDevice.toString());
                    break;
                case LoopPro.Loop_GET_HOST_INFO:
                    loopHost = (LoopHost) msg.obj;
                    Log.d("LoopHost", loopHost.toString());
                    break;
                case LoopPro.LOOP_GET_DEVICE_INFO:
                    LoopDevice loopDevice = (LoopDevice) msg.obj;
                    Log.d("LoopDevice", loopDevice.toString());
                    break;
                case LoopPro.LOOP_MESSAGE:
                    LoopMessage loopMessage = (LoopMessage) msg.obj;
                    Log.d("LoopMessage", loopMessage.toString());
                    break;
            }
        }
    }
}