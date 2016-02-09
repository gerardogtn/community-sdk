package com.emotiv.motiondatalogger;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.emotiv.insight.IEdk;
import com.example.com.emotiv.eeglogger.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;

    private boolean mIsSampling = false;
    private MotionDataWriterHandlerThread mHandlerThread;
    private Thread mSamplerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        promptBluetoothConnectionIfNeeded();
    }

    private void promptBluetoothConnectionIfNeeded() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @OnClick(R.id.startbutton)
    public void onStartButtonClick() {
        if (!mIsSampling) {
            if (connectToEmoEngine()) {
                setUpHandlerThread();
                startSamplingThread();
            } else {
                Toast.makeText(this, "Cannot connect to Emotiv device", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Already sampling emotiv device", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean connectToEmoEngine() {
        IEdk.IEE_EngineConnect(this, "");
        IEdk.IEE_MotionDataCreate();
        if (IEdk.IEE_GetInsightDeviceCount() != 0) {
            IEdk.IEE_ConnectInsightDevice(0);
            return true;
        } else {
            return false;
        }
    }

    private void setUpHandlerThread() {
        mHandlerThread = new MotionDataWriterHandlerThread();
        mHandlerThread.start();
        mHandlerThread.getLooper();
        Log.i(TAG, "setUpHandlerThread: eegWriterHandlerThread created");
    }

    private void startSamplingThread() {
        mIsSampling = true;
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
            stopSamplingThread();
        } else {
            Toast.makeText(this, "There is no sampling going on right now", Toast.LENGTH_SHORT).show();
        }

    }

    private void stopSamplingThread() {
        mIsSampling = false;
        mSamplerThread.interrupt();
        mSamplerThread = null;
        MotionDataWriter.stopWriting();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandlerThread.quit();
    }


}
