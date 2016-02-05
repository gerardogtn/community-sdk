package com.emotiv.motiondatalogger;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import com.emotiv.insight.IEdk;
import com.emotiv.insight.IEdk.IEE_Event_t;
import com.emotiv.insight.IEdkErrorCode;
import com.example.com.emotiv.eeglogger.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import butterknife.OnClick;

;

public class MainActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter mBluetoothAdapter;

	private boolean lock = false;
	private boolean isEnablGetData = false;
	private boolean isEnableWriteFile = false;

	int userId;
	private BufferedWriter motionWriter;

	Button Start_button,Stop_button;
	IEdk.IEE_MotionDataChannel_t[] Channel_list = {IEdk.IEE_MotionDataChannel_t.IMD_COUNTER, IEdk.IEE_MotionDataChannel_t.IMD_GYROX,IEdk.IEE_MotionDataChannel_t.IMD_GYROY,
			IEdk.IEE_MotionDataChannel_t.IMD_GYROZ,IEdk.IEE_MotionDataChannel_t.IMD_ACCX,IEdk.IEE_MotionDataChannel_t.IMD_ACCY,IEdk.IEE_MotionDataChannel_t.IMD_ACCZ,
			IEdk.IEE_MotionDataChannel_t.IMD_MAGX,IEdk.IEE_MotionDataChannel_t.IMD_MAGY,IEdk.IEE_MotionDataChannel_t.IMD_MAGZ,IEdk.IEE_MotionDataChannel_t.IMD_TIMESTAMP};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

		mBluetoothAdapter = bluetoothManager.getAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}


		//Connect to emoEngine
		IEdk.IEE_EngineConnect(this, "");
		IEdk.IEE_MotionDataCreate();
		Thread processingThread=new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				while(true)
				{
					try
					{
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(1);
						if(isEnablGetData && isEnableWriteFile)handler.sendEmptyMessage(2);
						Thread.sleep(5);
					}

					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		};
		processingThread.start();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

				case 0:
					int state = IEdk.IEE_EngineGetNextEvent();
					if (state == IEdkErrorCode.EDK_OK.ToInt()) {
						int eventType = IEdk.IEE_EmoEngineEventGetType();
						userId = IEdk.IEE_EmoEngineEventGetUserId();
						if(eventType == IEE_Event_t.IEE_UserAdded.ToInt()){
							Log.e("SDK","User added");
							isEnablGetData = true;
						}
						if(eventType == IEE_Event_t.IEE_UserRemoved.ToInt()){
							Log.e("SDK","User removed");
							isEnablGetData = false;
						}
					}

					break;
				case 1:
					int number = IEdk.IEE_GetInsightDeviceCount();
					if(number != 0) {
						if(!lock){
							lock = true;
							IEdk.IEE_ConnectInsightDevice(0);
						}
					}
					else lock = false;
					break;
				case 2:
					IEdk.IEE_MotionDataUpdateHandle(userId);
					int sample = IEdk.IEE_MotionDataGetNumberOfSample(userId);
					if(sample > 0){
						for(int sampleIdx =0; sampleIdx < sample; sampleIdx++)
						{
							for(int j=0;j< Channel_list.length;j++){
								double[] eeg_data = IEdk.IEE_MotionDataGet(Channel_list[j]);
								addData(eeg_data[sampleIdx]);
							}
							try {
								motionWriter.newLine();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					break;
			}

		}

	};

	@OnClick(R.id.startbutton)
	public void onStartButtonClick(){
		Log.e("MotionLogger","Start Write File");
		setDataFile();
		isEnableWriteFile = true;
	}

	@OnClick(R.id.stopbutton)
	public void onStopButtonClick(){
		Log.e("MotionLogger", "Stop Write File");
		stopWriteFile();
		isEnableWriteFile = false;
	}

	private void setDataFile() {
		try {
			String eeg_header = "COUNTER_MEMS,GYROX,GYROY,GYROZ,ACCX,ACCY,ACCZ,MAGX,MAGY,MAGZ,TimeStamp";
			File root = Environment.getExternalStorageDirectory();
			String file_path = root.getAbsolutePath()+ "/MotionLogger/";
			File folder=new File(file_path);
			if(!folder.exists()) {
				folder.mkdirs();
			}
			motionWriter = new BufferedWriter(new FileWriter(file_path+"raw_motion.csv"));
			motionWriter.write(eeg_header);
			motionWriter.newLine();
		} catch (Exception e) {
			Log.e("","Exception"+ e.getMessage());
		}
	}
	private void stopWriteFile(){
		try {
			motionWriter.flush();
			motionWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * public void addEEGData(Double[][] eegs) Add EEG Data for write int the
	 * EEG File
	 *
	 * @param data
	 *            - double array of eeg data
	 */
	public void addData(double data) {

		if (motionWriter == null) {
			return;
		}

		String input = "";
		input += (String.valueOf(data) + ",");
		try {
			motionWriter.write(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
