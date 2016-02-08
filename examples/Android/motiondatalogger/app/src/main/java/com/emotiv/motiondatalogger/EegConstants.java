package com.emotiv.motiondatalogger;

/**
 * Created by inspirecave on 5/02/16.
 */
public class EegConstants {

    public static final String COUNTER = "COUNTER_MEMS";
    public static final String TIMESTAMP = "TimeStamp";

    public static final String HEADER = getStringSeparatedByCommas(COUNTER,
            Gyroscope.X, Gyroscope.Y, Gyroscope.Z,
            Acc.X, Acc.Y, Acc.Z,
            Mag.X, Mag.Y, Mag.Z,
            TIMESTAMP);

    public static final String getStringSeparatedByCommas(String ... strings){
        String output = "";
        for (String s : strings){
         output = output + s + ",";
        }
        return output;
    }

    public static class Gyroscope {
        public static final String X = "GYROX";
        public static final String Y = "GYROY";
        public static final String Z = "GYROZ";
    }

    public static class Acc {
        public static final String X = "ACCX";
        public static final String Y = "ACCY";
        public static final String Z = "ACCZ";
    }

    public static class Mag {
        public static final String X = "MAGX";
        public static final String Y = "MAGY";
        public static final String Z = "MAGZ";
    }

}
