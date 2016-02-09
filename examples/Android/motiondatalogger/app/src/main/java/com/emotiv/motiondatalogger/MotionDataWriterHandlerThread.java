package com.emotiv.motiondatalogger;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.emotiv.insight.IEdk;

/**
 * Created by gerardogtn on 2/8/16.
 */
public class MotionDataWriterHandlerThread extends HandlerThread {

    private static final String TAG = "MotionDataWriterHandlerThread";

    private static int mUserId = 0;
    private static WriterHandler sWriterHandler;

    public MotionDataWriterHandlerThread() {
        super(TAG);
        mUserId = IEdk.IEE_EmoEngineEventGetUserId();
    }

    @Override
    protected void onLooperPrepared() {
        sWriterHandler = new WriterHandler();
    }

    public void queueEegData(MessageType messageType) {
        sWriterHandler.obtainMessage(messageType.getValue()).sendToTarget();
    }

    public enum MessageType {
        ADD_MOTION_DATA(0);

        private final int VALUE;

        MessageType(int VALUE) {
            this.VALUE = VALUE;
        }

        public int getValue() {
            return VALUE;
        }
    }

    private static class WriterHandler extends Handler {

        public WriterHandler() {

        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MessageType.ADD_MOTION_DATA.getValue()) {
                writeMotionData();
            }
        }

        private void writeMotionData() {
            IEdk.IEE_MotionDataUpdateHandle(mUserId);
            int numberOfSamples = IEdk.IEE_MotionDataGetNumberOfSample(mUserId);
            for (int i = 0; i < numberOfSamples; i++) {
                double[] motionData = getMotionData();
                MotionDataWriter.writeData(motionData, MotionDataConstants.NUMBER_OF_MOTION_DATA_CHANNELS);
            }
        }

        private double[] getMotionData() {
            double[] data = new double[MotionDataConstants.NUMBER_OF_MOTION_DATA_CHANNELS];

            for (int j = 0; j < MotionDataConstants.NUMBER_OF_MOTION_DATA_CHANNELS; j++) {
                data[j] = IEdk.IEE_MotionDataGet(MotionDataConstants.MOTION_DATA_CHANNELS[j])[0];
            }

            return data;
        }


    }
}
