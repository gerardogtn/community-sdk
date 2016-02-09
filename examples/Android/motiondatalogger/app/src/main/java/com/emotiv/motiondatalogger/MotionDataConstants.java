package com.emotiv.motiondatalogger;

import com.emotiv.insight.IEdk;

/**
 * Created by gerardogtn on 2/8/16.
 */
public class MotionDataConstants {

    public static final IEdk.IEE_MotionDataChannel_t[] MOTION_DATA_CHANNELS = {
            IEdk.IEE_MotionDataChannel_t.IMD_COUNTER,
            IEdk.IEE_MotionDataChannel_t.IMD_GYROX, IEdk.IEE_MotionDataChannel_t.IMD_GYROY,
            IEdk.IEE_MotionDataChannel_t.IMD_GYROZ, IEdk.IEE_MotionDataChannel_t.IMD_ACCX,
            IEdk.IEE_MotionDataChannel_t.IMD_ACCY, IEdk.IEE_MotionDataChannel_t.IMD_ACCZ,
            IEdk.IEE_MotionDataChannel_t.IMD_MAGX, IEdk.IEE_MotionDataChannel_t.IMD_MAGY,
            IEdk.IEE_MotionDataChannel_t.IMD_MAGZ, IEdk.IEE_MotionDataChannel_t.IMD_TIMESTAMP
    };

    public static final int NUMBER_OF_MOTION_DATA_CHANNELS = 11;


    private static final String COUNTER = "COUNTER_MEMS";
    private static final String TIMESTAMP = "TimeStamp";

    public static final String MOTION_DATA_HEADER = getStringSeparatedByCommas(COUNTER,
            Gyroscope.X, Gyroscope.Y, Gyroscope.Z,
            Acc.X, Acc.Y, Acc.Z,
            Mag.X, Mag.Y, Mag.Z,
            TIMESTAMP);

    public static String getStringSeparatedByCommas(String ... strings){
        String output = "";
        for (String s : strings){
            output = output + s + ",";
        }
        return output;
    }

    private static class Gyroscope {
        public static final String X = "GYROX";
        public static final String Y = "GYROY";
        public static final String Z = "GYROZ";
    }

    private static class Acc {
        public static final String X = "ACCX";
        public static final String Y = "ACCY";
        public static final String Z = "ACCZ";
    }

    private static class Mag {
        public static final String X = "MAGX";
        public static final String Y = "MAGY";
        public static final String Z = "MAGZ";
    }

}
