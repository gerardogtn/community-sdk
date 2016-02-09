package com.emotiv.motiondatalogger;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by inspirecave on 5/02/16.
 */
public class MotionDataWriter {

    private static final String DIRECTORY = "/MotionDataLogger";
    private static final String FILE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + DIRECTORY;

    private static final String BASE_FILENAME = "/%srawMotionData.csv";
    public static final String FILENAME_WITH_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + DIRECTORY + BASE_FILENAME;

    private static BufferedWriter mWriter;

    public MotionDataWriter() {
        createDirectory();
    }

    private static void createDirectory() {
        File directory = new File(FILE_PATH);
        directory.mkdir();
    }

    public static void startWriting(){
        try {
            Date currentDate = new Date();
            FileWriter fileWriter = new FileWriter(String.format(FILENAME_WITH_PATH, currentDate.toString()));
            mWriter = new BufferedWriter(fileWriter);
            mWriter.write(MotionDataConstants.MOTION_DATA_HEADER + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopWriting(){
        try {
            mWriter.flush();
            mWriter.close();
            mWriter = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeData(double data[], int size) {

        if (mWriter == null) {
            return;
        }

        try {
            String input = "";
            for (int i = 0; i < size - 1; i++){
                input += (String.valueOf(data[i]) + ",");
            }
            input += String.valueOf(data[size]) + "\n";
            mWriter.write(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
