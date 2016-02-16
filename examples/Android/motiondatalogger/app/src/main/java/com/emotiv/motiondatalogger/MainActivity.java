package com.emotiv.motiondatalogger;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.emotiv.insight.IEdk;
import com.example.com.emotiv.eeglogger.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;

    private boolean mIsSampling = false;
    private boolean mIsConnectedToemotiv = false;
    private MotionDataWriterHandlerThread mHandlerThread;
    private Thread mSamplerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        promptBluetoothConnectionIfNeeded();
        connectToEmoEngine();
    }

    private void promptBluetoothConnectionIfNeeded() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @OnClick(R.id.btn_connect_to_emotiv)
    public void onConnectToEmotivButtonClick(){
        if (mIsConnectedToemotiv){
            Toast.makeText(this, "Already connected to emotiv!", Toast.LENGTH_SHORT).show();
        } else {
            connectToEmoEngine();
        }
    }

    private void connectToEmoEngine() {
        IEdk.IEE_EngineConnect(this, "");
        IEdk.IEE_MotionDataCreate();
        Log.i(TAG, "connectToEmoEngine: insight device count: " + String.valueOf(IEdk.IEE_GetInsightDeviceCount()));
        if (IEdk.IEE_GetInsightDeviceCount() != 0) {
            IEdk.IEE_ConnectInsightDevice(0);
            mIsConnectedToemotiv = true;
        } else {
            mIsConnectedToemotiv = false;
            Toast.makeText(this, "Could not connect to Emotiv device", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.startbutton)
    public void onStartButtonClick() {
        if (!mIsSampling) {
            if (mIsConnectedToemotiv) {
                setUpHandlerThread();
                startSamplingThread();
            } else {
                Toast.makeText(this, "Not connected to Emotiv device", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Already sampling emotiv device", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpHandlerThread() {
        mIsSampling = true;
        mHandlerThread = new MotionDataWriterHandlerThread();
        mHandlerThread.start();
        mHandlerThread.getLooper();
        Log.i(TAG, "setUpHandlerThread: eegWriterHandlerThread created");
    }

    private void startSamplingThread() {
        MotionDataWriter.startWriting();

        mSamplerThread = new Thread() {
            @Override
            public void run() {
                while (mIsSampling) {
                    try {
                        mHandlerThread.queueEegData(MotionDataWriterHandlerThread.MessageType.ADD_MOTION_DATA);
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
    }

    @OnClick(R.id.stopbutton)
    public void onStopButtonClick() {
        if (mIsSampling) {
            stopSampling();
        } else {
            Toast.makeText(this, "There is no sampling going on right now", Toast.LENGTH_SHORT).show();
        }

    }

    private void stopSampling() {
        mIsSampling = false;
        stopSamplingThread();
        mHandlerThread.quit();
    }

    private void stopSamplingThread() {
        mSamplerThread.interrupt();
        mSamplerThread = null;
        MotionDataWriter.stopWriting();
    }

    @OnClick(R.id.btn_disconnect_emotiv)
    public void onDisconnectFromEmotivButtonClick(){
        if (mIsConnectedToemotiv){
            IEdk.IEE_EngineDisconnect();
            mIsConnectedToemotiv = false;
        } else {
            Toast.makeText(this, "Not connected to Emotiv device", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsConnectedToemotiv) {
            IEdk.IEE_EngineDisconnect();
        }
        if (mIsSampling) {
            stopSampling();
        }
    }


}
