package com.taberu.sensorsmeter;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

//import java.io.FileOutputStream;

/**
 * Created by Taberu on 11/11/2016.
 */

public class FileServices {

    boolean appendFile(Context context, String sBody) {
//        String sFilePath = "filescsv";
        String sFileName = "driver_data.csv";

//        String filename = "myfile";
//        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(sFileName, Context.MODE_APPEND);
            outputStream.write(sBody.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        File myDir = new File(context.getFilesDir().toString() + "/" + sFilePath);
////        myDir.mkdir(); //create folders where write files
//        if(!myDir.exists()) {
//            if (myDir.mkdir()) {
//                Log.e("<<GUILHERME>>", "TRUE");
//            } else {
//                Log.e("<<GUILHERME>>", "FALSE");
//            }
//        }

//        File myFile = new File(myDir, sFileName);
//
//        try {
//            FileOutputStream outputStream = new FileOutputStream(myFile, context.MODE_PRIVATE);
//            outputStream.write(sBody.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return true;
    }

    boolean clearFiles(Context context) {
        File dirFiles = context.getFilesDir();

        String[] listFiles = dirFiles.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".csv");
            }
        });

        for (String strFile : listFiles) {
            // strFile is the file name
            context.deleteFile(strFile);
        }

        return true;
    }
}
